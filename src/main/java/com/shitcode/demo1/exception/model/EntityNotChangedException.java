package com.shitcode.demo1.exception.model;

public class EntityNotChangedException extends RuntimeException {
    public EntityNotChangedException() {
        super();
    }

    public EntityNotChangedException(String message) {
        super(message);
    }

    public EntityNotChangedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}