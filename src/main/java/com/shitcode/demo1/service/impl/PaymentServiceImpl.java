package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.util.concurrent.AtomicDouble;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.dto.DiscountDTO;
import com.shitcode.demo1.dto.DiscountDTO.SimpleResponse;
import com.shitcode.demo1.dto.PaymentDTO;
import com.shitcode.demo1.dto.PaymentDTO.ProductQuantityDTO;
import com.shitcode.demo1.dto.PaymentDTO.Response;
import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.entity.PaypalTransaction;
import com.shitcode.demo1.entity.Recipe;
import com.shitcode.demo1.entity.Recipe.RecipeStatus;
import com.shitcode.demo1.entity.RecipeProduct;
import com.shitcode.demo1.exception.model.DiscountOverTimeException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.OutOfStockException;
import com.shitcode.demo1.mapper.RecipeMapper;
import com.shitcode.demo1.properties.FontendServerConfigData;
import com.shitcode.demo1.properties.LvoxxServerConfigData;
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
    private final RecipeProductRepository recipeProductRepository;
    private final ProductService productService;
    private final RecipeMapper recipeMapper;
    private final FontendServerConfigData fontendServerConfigData;
    private final LvoxxServerConfigData lvoxxServerConfigData;

    private final String APPROVAL_LINK = "approval_url";
    private final String PAYMENT_STATE = "approved";
    private final String METHOD = "Paypal";
    private final String CURRENCY = "USD";

    public PaymentServiceImpl(PaypalService paypalService, ProductService productService,
            FontendServerConfigData fontendServerConfigData, RecipeRepository recipeRepository,
            RecipeProductRepository recipeProductRepository,
            RecipeMapper recipeMapper, LvoxxServerConfigData lvoxxServerConfigData) {
        this.recipeRepository = recipeRepository;
        this.paypalService = paypalService;
        this.productService = productService;
        this.recipeMapper = recipeMapper;
        this.fontendServerConfigData = fontendServerConfigData;
        this.recipeProductRepository = recipeProductRepository;
        this.lvoxxServerConfigData = lvoxxServerConfigData;
    }

    private String successBackendUrl;
    private String cancelBackendUrl;
    private String successFontendUrl;
    private String cancelFontendUrl;
    private String errorFontendUrl;

    @PostConstruct
    void setUp() {
        successFontendUrl = fontendServerConfigData.getPayment().getSuccess();
        cancelFontendUrl = fontendServerConfigData.getPayment().getCancel();
        errorFontendUrl = fontendServerConfigData.getPayment().getError();
        successBackendUrl = lvoxxServerConfigData.getBaseServerUrl()
                .concat(lvoxxServerConfigData.getPayment().getSuccessPath());
        cancelBackendUrl = lvoxxServerConfigData.getBaseServerUrl()
                .concat(lvoxxServerConfigData.getPayment().getCancelPath());
    }

    @Override
    public PaymentDTO.Response createPaymentAndRedirectToCheckOutPage(PaymentDTO.Request request,
            UserDetails userDetails)
            throws PayPalRESTException {
        PaymentDTO.Response res = null;
        try {
            AtomicDouble total = new AtomicDouble(0.0);
            List<RecipeProduct> recipeProduct = new ArrayList<>();
            // Calc total prices base on product and its discount
            for (ProductQuantityDTO product : request.getProducts()) {
                ProductDTO.ProductWithCategoryAndDiscountResponse prodRes = productService
                        .findProductWithCategoryAndDiscount(product.getProductId());
                if (prodRes.getAvailableQuatity() < product.getQuantity()) {
                    throw new OutOfStockException(
                            String.format("Product %s is out of stock, try again later.", prodRes.getName()));
                }
                CategoryDTO.Response ctgRes = prodRes.getCategory();
                DiscountDTO.SimpleResponse disRes = Optional.ofNullable(prodRes.getDiscount())
                        .orElse(SimpleResponse.builder()
                                .title(null)
                                .type(null)
                                .salesPercentAmount(Double.valueOf(0.0))
                                .expDate(null)
                                .build());
                Double tempTotal = prodRes.getPrice().doubleValue() * product.getQuantity();
                Double subTotal = tempTotal - (tempTotal * disRes.getSalesPercentAmount());
                total.addAndGet(subTotal);
                RecipeProduct recipe = RecipeProduct.builder()
                        .categoryId(ctgRes.getId())
                        .categoryName(ctgRes.getName())
                        .productId(prodRes.getId())
                        .productName(prodRes.getName())
                        .quantity(product.getQuantity())
                        .price(prodRes.getPrice())
                        .discountName(
                                disRes.getTitle())
                        .discountType(
                                disRes.getType())
                        .discountAmount(disRes.getSalesPercentAmount())
                        .subTotal(subTotal)
                        .build();
                if (disRes.getExpDate() != null) {
                    if (disRes.getExpDate().isBefore(OffsetDateTime.now())) {
                        throw new DiscountOverTimeException(
                                String.format("Discount %s has been expired, try again later.",
                                        disRes.getTitle()));
                    }
                }

                recipeProduct.add(recipe);
            }
            Double totalPrice = total.get() - request.getShippingFee();
            // Create paypal transaction
            Payment payment = paypalService.createPayment(
                    totalPrice,
                    CURRENCY,
                    METHOD,
                    "sale",
                    request.getDescription(),
                    cancelBackendUrl,
                    successBackendUrl); // README: If you want to set success url for other website, you can set it
                                        // here.

            // Get approval url
            String approvalUrl = null;
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals(APPROVAL_LINK)) {
                    approvalUrl = links.getHref();
                }
            }

            // Save to database
            PaypalTransaction paypalTransaction = PaypalTransaction.builder()
                    .transactionId(payment.getId())
                    .amount(totalPrice)
                    .transactionDate(LocalDateTime.now())
                    .build();
            paypalTransaction = paypalService.save(paypalTransaction);
            recipeProduct = recipeProductRepository.saveAll(recipeProduct);
            Recipe recipe = Recipe.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .shippingAddress(request.getShippingAddress())
                    .shippingFee(request.getShippingFee())
                    .username(userDetails.getUsername())
                    .total(totalPrice)
                    .status(RecipeStatus.PENDING)
                    .recipeProducts(recipeProduct)
                    .paypalTransaction(paypalTransaction)
                    .build();
            recipe = recipeRepository.save(recipe);

            // Map to response
            res = recipeMapper.toRecipeResponse(recipe);
            res.setRedirectToPayoutUrl(approvalUrl);
        } catch (PayPalRESTException | EntityNotFoundException | DiscountOverTimeException e) {
            LogPrinter.printServiceLog(Type.ERROR, PaymentServiceImpl.class.getName(),
                    "createPaymentAndRedirectToCheckOutPage", e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public RedirectView executePaymentAndRedirectToSuccessPage(String paymentId, String payerId)
            throws PayPalRESTException {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (!payment.getState().equals(PAYMENT_STATE)) {
                return new RedirectView(cancelFontendUrl);
            }
            PaypalTransaction paypalTransaction = paypalService.findByTransactionId(paymentId);
            Recipe recipe = recipeRepository.findById(paypalTransaction.getRecipe().getId()).get();
            recipe.setStatus(RecipeStatus.SUCCESS);
            recipeRepository.save(recipe);

            paypalService.save(paypalTransaction);
            return new RedirectView(successFontendUrl);
        } catch (PayPalRESTException e) {
            LogPrinter.printServiceLog(Type.ERROR, PaymentServiceImpl.class.getName(),
                    "createPaymentAndRedirectToCheckOutPage", e.getMessage());
        }
        return new RedirectView(errorFontendUrl);
    }

    @Override
    public Response findByRecipeId(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
        return recipeMapper.toRecipeResponse(recipe);
    }
}
