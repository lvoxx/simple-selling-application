package com.shitcode.demo1.exception.model;

public class ImageEncodeException extends RuntimeException {
    public ImageEncodeException() {
        super();
    }

    public ImageEncodeException(String message) {
        super(message);
    }

    public ImageEncodeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}