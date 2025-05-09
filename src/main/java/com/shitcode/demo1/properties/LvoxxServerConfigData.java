package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "lvoxx-server")
public class LvoxxServerConfigData {
    private ServerConfig devServer;
    private ServerConfig prodServer;
    private ServerConfig fontendServer;
    private ContactConfig contact;
    private LicenseConfig license;
    private InfoConfig info;
    private ExternalDocumentationConfig externalDocumentation;
    private boolean productDeploy;
    private PaymentConfig payment;

    public String getBaseServerUrl() {
        return (productDeploy ? prodServer : devServer).getBaseUrl();
    }

    public String getPaymentBaseUrl() {
        return getBaseServerUrl() + payment.getBasePath();
    }

    public String getPaymentSuccessUrl() {
        return getPaymentBaseUrl() + payment.getSuccessPath();
    }

    public String getPaymentCancelUrl(String name) {
        return String.format(getPaymentBaseUrl() + payment.getCancelPath(), name);
    }

    public String getPaymentErrorUrl(String name) {
        return String.format(getPaymentBaseUrl() + payment.getErrorPath(), name);
    }

    @Data
    public static class ServerConfig {
        private String baseUrl;
        private String description;
    }

    @Data
    public static class PaymentConfig {
        private String basePath;
        private String successPath;
        private String cancelPath;
        private String errorPath;
    }

    @Data
    public static class ContactConfig {
        private String name;
        private String email;
        private String url;
    }

    @Data
    public static class LicenseConfig {
        private String name;
        private String url;
    }

    @Data
    public static class InfoConfig {
        private String title;
        private String version;
        private String description;
    }

    @Data
    public static class ExternalDocumentationConfig {
        private String description;
        private String url;
    }
}
