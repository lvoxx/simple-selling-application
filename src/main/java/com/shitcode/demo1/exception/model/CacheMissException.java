package com.shitcode.demo1.exception.model;

public class CacheMissException extends RuntimeException {
    public CacheMissException() {
        super();
    }

    public CacheMissException(String message) {
        super(message);
    }

    public CacheMissException(String message, Throwable throwable) {
        super(message, throwable);
    }
}