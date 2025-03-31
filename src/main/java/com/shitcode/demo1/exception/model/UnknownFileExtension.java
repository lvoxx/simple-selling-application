package com.shitcode.demo1.exception.model;

public class UnknownFileExtension extends RuntimeException {
    public UnknownFileExtension() {
        super();
    }

    public UnknownFileExtension(String message) {
        super(message);
    }

    public UnknownFileExtension(String message, Throwable throwable) {
        super(message, throwable);
    }
}