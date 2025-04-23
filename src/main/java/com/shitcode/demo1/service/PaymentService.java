package com.shitcode.demo1.service;

import org.springframework.web.servlet.view.RedirectView;

import com.paypal.base.rest.PayPalRESTException;
import com.shitcode.demo1.dto.PaymentDTO;

public interface PaymentService {
    RedirectView createPaymentAndRedirectToCheckOutPage(PaymentDTO.Request request) throws PayPalRESTException;

    RedirectView executePaymentAndRedirectToSuccessPage(String paymentId, String payerId) throws PayPalRESTException;
}