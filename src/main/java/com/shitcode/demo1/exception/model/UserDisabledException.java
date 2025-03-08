package com.shitcode.demo1.exception.model;

public class UserDisabledException extends RuntimeException {
    public UserDisabledException() {
        super();
    }

    public UserDisabledException(String message) {
        super(message);
    }

    public UserDisabledException(String message, Throwable throwable) {
        super(message, throwable);
    }
}