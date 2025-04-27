package com.shitcode.demo1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.PaymentDTO;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.PaymentService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.Data;

@Data
@RestController
@LogCollector
@RequestMapping("/payment")
@Tag(name = "Payment Controller", description = "APIs for managing payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final ResponseService responseService;
    private final RateLimiterConfigData rateLimiterConfigData;

    public PaymentController(PaymentService paymentService, ResponseService responseService,
            RateLimiterConfigData rateLimiterConfigData) {
        this.paymentService = paymentService;
        this.responseService = responseService;
        this.rateLimiterConfigData = rateLimiterConfigData;
    }

    private RateLimiterPlan PAYOUT;
    private RateLimiterPlan PAYMENT_ID;
    private RateLimiterPlan PAYMENTS;

    @PostConstruct
    void setUp() {
        PAYOUT = rateLimiterConfigData.getRateLimiterPlan("payment", "payout");
        PAYMENT_ID = rateLimiterConfigData.getRateLimiterPlan("payment", "payment-id");
        PAYMENTS = rateLimiterConfigData.getRateLimiterPlan("payment", "payments");
    }

    @PostMapping("/payout")
    @PreAuthorize("isAuthenticated()")
    public RedirectView pay(@RequestBody PaymentDTO.Request request,
            @AuthenticationPrincipal UserDetails userDetails)
            throws Exception {
        return responseService.execute(
                () -> paymentService.createPaymentAndRedirectToCheckOutPage(request, userDetails),
                PAYOUT);
    }

}
