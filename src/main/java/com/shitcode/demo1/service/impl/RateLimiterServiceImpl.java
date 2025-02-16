package com.shitcode.demo1.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.service.RateLimiterService;
import com.shitcode.demo1.utils.LoggingModel;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.github.bucket4j.Bucket;

@Service
@LogCollector(loggingModel =  LoggingModel.SERVICE)
public class RateLimiterServiceImpl implements RateLimiterService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ipAddress, RateLimiterPlan plan) {
        switch (plan) {
            case RateLimiterPlan.SOFT:
                return cache.computeIfAbsent(ipAddress, this::newSoftBucket);
            case RateLimiterPlan.MEDIUM:
                return cache.computeIfAbsent(ipAddress, this::newMediumBucket);
            case RateLimiterPlan.HARD:
                return cache.computeIfAbsent(ipAddress, this::newHardBucket);
            default:
                return cache.computeIfAbsent(ipAddress, this::newBasicBucket);
        }
    }

    private Bucket newSoftBucket(String ipAddress) {
        return Bucket.builder()
                .addLimit(RateLimiterPlan.MAX.getLimit())
                .addLimit(RateLimiterPlan.SOFT.getLimit())
                .build();
    }

    private Bucket newMediumBucket(String ipAddress) {
        return Bucket.builder()
                .addLimit(RateLimiterPlan.MAX.getLimit())
                .addLimit(RateLimiterPlan.MEDIUM.getLimit())
                .build();
    }

    private Bucket newHardBucket(String ipAddress) {
        return Bucket.builder()
                .addLimit(RateLimiterPlan.MAX.getLimit())
                .addLimit(RateLimiterPlan.HARD.getLimit())
                .build();
    }

    private Bucket newBasicBucket(String ipAddress) {
        return Bucket.builder()
                .addLimit(RateLimiterPlan.MAX.getLimit())
                .addLimit(RateLimiterPlan.BASIC.getLimit())
                .build();
    }
}
