package com.shitcode.demo1.exception.model;

public class VideoEncodeException extends RuntimeException {
    public VideoEncodeException() {
        super();
    }

    public VideoEncodeException(String message) {
        super(message);
    }

    public VideoEncodeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}