package com.shitcode.demo1.service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.shitcode.demo1.entity.PaypalTransaction;

public interface PaypalService {
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException;

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

    public PaypalTransaction findByTransactionId(String transactionId);

    public PaypalTransaction save(PaypalTransaction paypalTransaction);
}