package com.shitcode.demo1.exception.model;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}