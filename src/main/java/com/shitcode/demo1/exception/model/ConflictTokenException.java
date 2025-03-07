package com.shitcode.demo1.exception.model;

public class ConflictTokenException extends RuntimeException {
    public ConflictTokenException() {
        super();
    }

    public ConflictTokenException(String message) {
        super(message);
    }

    public ConflictTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }
}