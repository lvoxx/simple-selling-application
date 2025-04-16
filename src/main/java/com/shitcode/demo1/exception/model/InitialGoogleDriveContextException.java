package com.shitcode.demo1.exception.model;

public class InitialGoogleDriveContextException extends RuntimeException {
    public InitialGoogleDriveContextException() {
        super();
    }

    public InitialGoogleDriveContextException(String message) {
        super(message);
    }

    public InitialGoogleDriveContextException(String message, Throwable throwable) {
        super(message, throwable);
    }
}