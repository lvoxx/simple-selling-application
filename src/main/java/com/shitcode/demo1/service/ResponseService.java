package com.shitcode.demo1.service;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.function.ThrowingSupplier;

import com.shitcode.demo1.utils.RateLimiterPlan;

public interface ResponseService {
    ResponseEntity<?> mapping(@NonNull ThrowingSupplier<ResponseEntity<?>> service,
            @NonNull RateLimiterPlan plan) throws Exception;

    <T> T execute(
            @NonNull ThrowingSupplier<T> service,
            @NonNull RateLimiterPlan plan) throws Exception;
}