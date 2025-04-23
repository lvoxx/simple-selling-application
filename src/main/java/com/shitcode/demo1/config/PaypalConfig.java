package com.shitcode.demo1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.shitcode.demo1.properties.PaypalConfigData;

@Configuration
public class PaypalConfig {
    private final PaypalConfigData paypalConfigData;

    public PaypalConfig(PaypalConfigData paypalConfigData) {
        this.paypalConfigData = paypalConfigData;
    }

    @Bean
    public APIContext apiContext() {
        return new APIContext(paypalConfigData.getClientId(), paypalConfigData.getClientSecret(),
                paypalConfigData.getMode());
    }
}
