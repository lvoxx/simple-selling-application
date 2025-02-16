package com.shitcode.demo1.exception.model;

public class AspectException extends RuntimeException {
    public AspectException() {
        super();
    }

    public AspectException(String message) {
        super(message);
    }

    public AspectException(String message, Throwable throwable) {
        super(message, throwable);
    }
}