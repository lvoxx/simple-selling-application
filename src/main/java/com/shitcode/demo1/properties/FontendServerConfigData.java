package com.shitcode.demo1.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "fontend-server")
public class FontendServerConfigData {
    private String base;
    private String unknown;
    private Map<String, String> login;
    private Map<String, String> active;
    private Map<String, String> payment;
}
