package com.shitcode.demo1.service;

import com.shitcode.demo1.utils.RateLimiterPlan;

import io.github.bucket4j.Bucket;

public interface RateLimiterService {
    Bucket resolveBucket(String ipAddress, RateLimiterPlan plan);
}