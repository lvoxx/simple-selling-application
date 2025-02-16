package com.shitcode.demo1.exception.model;

public class UserUnAuthException extends RuntimeException {
    public UserUnAuthException() {
        super();
    }

    public UserUnAuthException(String message) {
        super(message);
    }

    public UserUnAuthException(String message, Throwable throwable) {
        super(message, throwable);
    }
}