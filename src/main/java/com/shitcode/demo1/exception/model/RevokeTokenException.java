package com.shitcode.demo1.exception.model;

public class RevokeTokenException extends RuntimeException {
    public RevokeTokenException() {
        super();
    }

    public RevokeTokenException(String message) {
        super(message);
    }

    public RevokeTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }
}