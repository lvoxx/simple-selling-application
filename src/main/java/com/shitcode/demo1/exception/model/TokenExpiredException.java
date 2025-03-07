package com.shitcode.demo1.exception.model;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super();
    }

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable throwable) {
        super(message, throwable);
    }
}