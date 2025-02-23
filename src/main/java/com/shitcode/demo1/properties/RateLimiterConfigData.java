package com.shitcode.demo1.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.shitcode.demo1.utils.RateLimiterPlan;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterConfigData {
    private Map<String, Map<String, RateLimiterPlan>> limiters;
}
