package com.shitcode.demo1.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientConfigData {
    private String name;
    private Map<String, String> roles;
}
