package com.shitcode.demo1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.shitcode.demo1.properties.PaypalConfigData;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Flag;
import com.shitcode.demo1.utils.LogPrinter.Type;

import jakarta.annotation.PostConstruct;

@Configuration
public class PaypalConfig {
    private final PaypalConfigData paypalConfigData;

    public PaypalConfig(PaypalConfigData paypalConfigData) {
        this.paypalConfigData = paypalConfigData;
    }

    @PostConstruct
    public void debugPaypalConfig() {
        LogPrinter.printLog(Type.INFO, Flag.START_UP, "CLIENT_ID: " + paypalConfigData.getClientId());
        LogPrinter.printLog(Type.INFO, Flag.START_UP, "CLIENT_SECRET: " + paypalConfigData.getClientSecret());
        LogPrinter.printLog(Type.INFO, Flag.START_UP, "MODE: " + paypalConfigData.getMode());
    }

    @Bean
    APIContext apiContext() {
        return new APIContext(paypalConfigData.getClientId(), paypalConfigData.getClientSecret(),
                paypalConfigData.getMode());
    }
}
