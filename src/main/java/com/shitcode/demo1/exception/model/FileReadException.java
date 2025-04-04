package com.shitcode.demo1.exception.model;

public class FileReadException extends RuntimeException {
    public FileReadException() {
        super();
    }

    public FileReadException(String message) {
        super(message);
    }

    public FileReadException(String message, Throwable throwable) {
        super(message, throwable);
    }
}