package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PaypalConfigData {
    private String clientId;
    private String clientSecret;
    private String mode;
}
