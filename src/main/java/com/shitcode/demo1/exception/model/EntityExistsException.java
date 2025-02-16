package com.shitcode.demo1.exception.model;

public class EntityExistsException extends RuntimeException {
    public EntityExistsException() {
        super();
    }

    public EntityExistsException(String message) {
        super(message);
    }

    public EntityExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}