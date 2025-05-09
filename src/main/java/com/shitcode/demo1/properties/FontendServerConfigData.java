package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "fontend-server")
public class FontendServerConfigData {
    private String base;
    private String error;
    private LoginConfig login;
    private ActiveConfig active;
    private PaymentConfig payment;

    public String getLoginUrl() {
        return base + login.getBase();
    }

    public String getErrorUrl(String reason) {
        return String.format(base + error, reason);
    }

    public String getActivateSuccessUrl() {
        return base + active.getSuccess();
    }

    public String getActivateDisabledUrl() {
        return base + active.getDisabled();
    }

    public String getActivateExpiredUrl() {
        return base + active.getExpired();
    }

    public String getPaymentBaseUrl() {
        return base + payment.getBasePath();
    }

    public String getPaymentCreateUrl(String name) {
        return String.format(getPaymentBaseUrl() + payment.getCreate(), name);
    }

    public String getPaymentSuccessUrl(String name) {
        return String.format(getPaymentBaseUrl() + payment.getSuccess(), name);
    }

    public String getPaymentCancelUrl(String name) {
        return String.format(getPaymentBaseUrl() + payment.getCancel(), name);
    }

    public String getPaymentErrorUrl(String name) {
        return String.format(getPaymentBaseUrl() + payment.getError(), name);
    }

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
        private String basePath;
        private String create;
        private String success;
        private String cancel;
        private String error;
    }

}
