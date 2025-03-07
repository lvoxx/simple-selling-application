package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientConfigData {
    private String name;
    private RoleConfig roles;
    
    @Data
    public static class RoleConfig {
        private String user;
        private String superUser;
        private String admin;
    }
}
