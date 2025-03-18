package com.shitcode.demo1.exception.model;

public class DiscountOverTimeException extends RuntimeException {
    public DiscountOverTimeException() {
        super();
    }

    public DiscountOverTimeException(String message) {
        super(message);
    }

    public DiscountOverTimeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}