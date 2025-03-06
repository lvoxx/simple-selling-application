package com.shitcode.demo1.exception.model;

public class ConflictRegistrationTokenException extends RuntimeException {
    public ConflictRegistrationTokenException() {
        super();
    }

    public ConflictRegistrationTokenException(String message) {
        super(message);
    }

    public ConflictRegistrationTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }
}