package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "fontend-server")
public class FontendServerConfigData {
    private String base;
    private String unknown;
    private LoginConfig login;
    private ActiveConfig active;
    private PaymentConfig payment;

    @Data
    public static class LoginConfig {
        private String base;
    }

    @Data
    public static class ActiveConfig {
        private String success;
        private String disabled;
        private String expired;
    }

    @Data
    public static class PaymentConfig {
        private String path;
        private String create;
        private String success;
        private String cancel;
        private String error;
    }

}
