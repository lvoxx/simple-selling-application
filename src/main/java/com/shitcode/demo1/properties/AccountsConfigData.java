package com.shitcode.demo1.properties;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "accounts")
public class AccountsConfigData {
    private ApplicationAccount root;

    @Data
    public static class ApplicationAccount {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private boolean locked;
        private boolean enabled;
        private BigDecimal points;
        private String[] roles;
    }
}
