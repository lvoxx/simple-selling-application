package com.shitcode.demo1.exception.model;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super();
    }

    public EmptyFileException(String message) {
        super(message);
    }

    public EmptyFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}