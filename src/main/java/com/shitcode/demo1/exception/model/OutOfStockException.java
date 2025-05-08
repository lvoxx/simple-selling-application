package com.shitcode.demo1.exception.model;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException() {
        super();
    }

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(String message, Throwable throwable) {
        super(message, throwable);
    }
}