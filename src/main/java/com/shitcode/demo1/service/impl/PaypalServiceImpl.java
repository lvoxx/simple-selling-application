package com.shitcode.demo1.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.entity.PaypalTransaction;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.repository.PaypalTransactionRepository;
import com.shitcode.demo1.service.PaypalService;
import com.shitcode.demo1.utils.LoggingModel;

/**
 * Paypal service implementation.
 * 
 * @author Ali Bouali <https://github.com/ali-bouali>
 * @see <a href=
 *      "https://github.dev/ali-bouali/springboot-3-paypal-integration">springboot-3-paypal-integration</a>
 */
@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class PaypalServiceImpl implements PaypalService {

    private final APIContext apiContext;
    private final PaypalTransactionRepository paypalTransactionRepository;
    private final MessageSource messageSource;

    public PaypalServiceImpl(APIContext apiContext, PaypalTransactionRepository paypalTransactionRepository,
            MessageSource messageSource) {
        this.apiContext = apiContext;
        this.paypalTransactionRepository = paypalTransactionRepository;
        this.messageSource = messageSource;
    }

    @Override
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total)); // 9.99$ - 9,99€

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }

    @Override
    public PaypalTransaction findByTransactionId(String transactionId) {
        return paypalTransactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage(
                        "exception.entity-not-found.paypal-transaction", new Object[] { transactionId },
                        Locale.getDefault())));
    }

    @Override
    public PaypalTransaction save(PaypalTransaction paypalTransaction) {
        return paypalTransactionRepository.save(paypalTransaction);
    }

}
