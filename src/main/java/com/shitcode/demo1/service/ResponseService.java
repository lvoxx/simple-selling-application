package com.shitcode.demo1.service;

import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import com.shitcode.demo1.utils.RateLimiterPlan;

public interface ResponseService {
    ResponseEntity<?> mapping(@NonNull Supplier<ResponseEntity<?>> service,
            @NonNull RateLimiterPlan plan);
}