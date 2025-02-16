package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth-token")
public class AuthTokenConfigData {
    private Integer registerExpDate;
    private String registerExpDateFormat;
    private Integer registerRevokeExpDate;
    private String registerRevokeExpDateFormat;
}