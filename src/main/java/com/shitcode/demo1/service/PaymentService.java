package com.shitcode.demo1.service;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.base.rest.PayPalRESTException;
import com.shitcode.demo1.dto.PaymentDTO;

public interface PaymentService {
    PaymentDTO.Response createPaymentAndRedirectToCheckOutPage(PaymentDTO.Request request, UserDetails userDetails)
            throws PayPalRESTException;

    RedirectView executePaymentAndRedirectToSuccessPage(String paymentId, String payerId) throws PayPalRESTException;

    PaymentDTO.Response findByRecipeId(UUID recipeId);

    RedirectView payoutIsCanceled(String recipeId);
}