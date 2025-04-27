package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.util.concurrent.AtomicDouble;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.PaymentDTO;
import com.shitcode.demo1.dto.PaymentDTO.ProductQuantityDTO;
import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.entity.PaypalTransaction;
import com.shitcode.demo1.entity.Recipe;
import com.shitcode.demo1.entity.Recipe.RecipeStatus;
import com.shitcode.demo1.entity.RecipeProduct;
import com.shitcode.demo1.exception.model.DiscountOverTimeException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.properties.FontendServerConfigData;
import com.shitcode.demo1.repository.PaypalTransactionRepository;
import com.shitcode.demo1.repository.RecipeProductRepository;
import com.shitcode.demo1.repository.RecipeRepository;
import com.shitcode.demo1.service.PaymentService;
import com.shitcode.demo1.service.PaypalService;
import com.shitcode.demo1.service.ProductService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Type;
import com.shitcode.demo1.utils.LoggingModel;

import jakarta.annotation.PostConstruct;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class PaymentServiceImpl implements PaymentService {

    private final PaypalService paypalService;
    private final RecipeRepository recipeRepository;
    private final PaypalTransactionRepository paypalTransactionRepository;
    private final RecipeProductRepository recipeProductRepository;
    private final ProductService productService;
    private final FontendServerConfigData fontendServerConfigData;

    private final String APPROVAL_LINK = "approval_url";
    private final String PAYMENT_STATE = "approved";
    private final String METHOD = "Paypal";
    private final String CURRENCY = "USD";

    public PaymentServiceImpl(PaypalService paypalService, ProductService productService,
            FontendServerConfigData fontendServerConfigData, RecipeRepository recipeRepository,
            PaypalTransactionRepository paypalTransactionRepository, RecipeProductRepository recipeProductRepository) {
        this.recipeRepository = recipeRepository;
        this.paypalTransactionRepository = paypalTransactionRepository;
        this.paypalService = paypalService;
        this.productService = productService;
        this.fontendServerConfigData = fontendServerConfigData;
        this.recipeProductRepository = recipeProductRepository;
    }

    private String successUrl;
    private String cancelUrl;
    private String errorUrl;

    @PostConstruct
    void setUp() {
        successUrl = fontendServerConfigData.getPayment().get("success");
        cancelUrl = fontendServerConfigData.getPayment().get("cancel");
        errorUrl = fontendServerConfigData.getPayment().get("error");
    }

    @Override
    public RedirectView createPaymentAndRedirectToCheckOutPage(PaymentDTO.Request request, UserDetails userDetails) throws PayPalRESTException {
        try {
            AtomicDouble total = new AtomicDouble(0.0);
            List<RecipeProduct> recipeProduct = new ArrayList<>();
            for (ProductQuantityDTO product : request.getProducts()) {
                ProductDTO.ProductWithCategoryAndDiscountResponse productEntity = productService
                        .findProductWithCategoryAndDiscount(product.getProductId());
                Double subTotal = productEntity.getPrice().doubleValue() * product.getQuantity();
                total.addAndGet(subTotal);
                RecipeProduct recipe = RecipeProduct.builder()
                        .categoryId(productEntity.getCategory().getId())
                        .categoryName(productEntity.getCategory().getName())
                        .productId(productEntity.getId())
                        .productName(productEntity.getName())
                        .quantity(product.getQuantity())
                        .price(productEntity.getPrice())
                        .subTotal(subTotal)
                        .build();
                if (productEntity.getDiscount() != null) {
                    if (productEntity.getDiscount().getExpDate().isBefore(OffsetDateTime.now())) {
                        throw new DiscountOverTimeException(
                                String.format("Discount %s has been expired, try again later.",
                                        productEntity.getDiscount().getTitle()));
                    }
                    recipe.setDiscountName(productEntity.getDiscount().getTitle());
                    recipe.setDiscountType(productEntity.getDiscount().getType());
                    recipe.setDiscountAmount(productEntity.getDiscount().getSalesPercentAmount());
                }
                recipeProduct.add(recipe);
            }

            // Create paypal transaction
            Payment payment = paypalService.createPayment(
                    total.get(),
                    CURRENCY,
                    METHOD,
                    "sale",
                    request.getDescription(),
                    cancelUrl,
                    successUrl);

            // Save to database
            PaypalTransaction paypalTransaction = PaypalTransaction.builder()
                    .transactionId(payment.getId())
                    .amount(total.get())
                    .transactionDate(LocalDateTime.now())
                    .build();
            paypalTransaction = paypalTransactionRepository.save(paypalTransaction);
            recipeProduct = recipeProductRepository.saveAll(recipeProduct);
            Recipe recipe = Recipe.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .shippingAddress(request.getShippingAddress())
                    .shippingFee(request.getShippingFee())
                    .username(userDetails.getUsername())
                    .total(total.get())
                    .status(RecipeStatus.PENDING)
                    .recipeProducts(recipeProduct)
                    .paypalTransaction(paypalTransaction)
                    .build();
            recipeRepository.save(recipe);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals(APPROVAL_LINK)) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException | EntityNotFoundException | DiscountOverTimeException e) {
            LogPrinter.printServiceLog(Type.ERROR, PaymentServiceImpl.class.getName(),
                    "createPaymentAndRedirectToCheckOutPage", e.getMessage());
            throw e;
        }
        return new RedirectView(errorUrl);
    }

    @Override
    public RedirectView executePaymentAndRedirectToSuccessPage(String paymentId, String payerId)
            throws PayPalRESTException {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals(PAYMENT_STATE)) {
                return new RedirectView(successUrl);
            }
            return new RedirectView(cancelUrl);
        } catch (PayPalRESTException e) {
            LogPrinter.printServiceLog(Type.ERROR, PaymentServiceImpl.class.getName(),
                    "createPaymentAndRedirectToCheckOutPage", e.getMessage());
        }
        return new RedirectView(errorUrl);
    }
}
