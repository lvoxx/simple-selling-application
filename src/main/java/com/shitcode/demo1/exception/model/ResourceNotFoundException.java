package com.shitcode.demo1.exception.model;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}