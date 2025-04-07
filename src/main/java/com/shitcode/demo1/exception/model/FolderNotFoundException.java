package com.shitcode.demo1.exception.model;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException() {
        super();
    }

    public FolderNotFoundException(String message) {
        super(message);
    }

    public FolderNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}