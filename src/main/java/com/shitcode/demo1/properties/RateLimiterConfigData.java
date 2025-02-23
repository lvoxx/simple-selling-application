package com.shitcode.demo1.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import com.shitcode.demo1.utils.RateLimiterPlan;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterConfigData {
    private final Map<String, Map<String, String>> limiters;

    public RateLimiterPlan getRateLimiterPlan(@NonNull String type, @NonNull String action) {
        if (limiters == null || !limiters.containsKey(type)) {
            return RateLimiterPlan.BASIC;
        }
        return RateLimiterPlan.valueOf(limiters.get(type).getOrDefault(action, "BASIC"));
    }

}
