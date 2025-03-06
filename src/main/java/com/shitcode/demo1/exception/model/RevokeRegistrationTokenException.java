package com.shitcode.demo1.exception.model;

public class RevokeRegistrationTokenException extends RuntimeException {
    public RevokeRegistrationTokenException() {
        super();
    }

    public RevokeRegistrationTokenException(String message) {
        super(message);
    }

    public RevokeRegistrationTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }
}