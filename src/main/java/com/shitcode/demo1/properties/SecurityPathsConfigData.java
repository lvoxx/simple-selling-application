package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "security-path")
public class SecurityPathsConfigData {
    private String[] everyone;
    private String[] user;
    private String[] superUser;
    private String[] admin;
}