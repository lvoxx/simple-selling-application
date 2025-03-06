package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "mailing")
public class MailingConfigData {
    private MailConfig registerEmail;

    @Data
    public static class MailConfig {
        private String subject;
        private String path;
        private String templateUrl;
    }
}
