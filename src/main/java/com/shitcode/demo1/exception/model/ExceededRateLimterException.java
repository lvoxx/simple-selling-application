package com.shitcode.demo1.exception.model;

public class ExceededRateLimterException extends RuntimeException {
    public ExceededRateLimterException() {
        super();
    }

    public ExceededRateLimterException(String message) {
        super(message);
    }

    public ExceededRateLimterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}