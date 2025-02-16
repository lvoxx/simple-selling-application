package com.shitcode.demo1.exception.model;

public class CacheEvictionException extends RuntimeException {
    public CacheEvictionException() {
        super();
    }

    public CacheEvictionException(String message) {
        super(message);
    }

    public CacheEvictionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}