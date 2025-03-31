package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "media")
public class MediaConfigData {
    private PathConfig path;
    private Boolean uploadLocally;

    @Data
    public static class PathConfig {
        private String root;
        private String images;
        private String videos;
    }
}