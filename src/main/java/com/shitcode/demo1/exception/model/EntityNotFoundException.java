package com.shitcode.demo1.exception.model;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}