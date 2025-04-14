package com.shitcode.demo1.exception.model;

public class UploadFileOnGoogleDriveException extends RuntimeException {
    public UploadFileOnGoogleDriveException() {
        super();
    }

    public UploadFileOnGoogleDriveException(String message) {
        super(message);
    }

    public UploadFileOnGoogleDriveException(String message, Throwable throwable) {
        super(message, throwable);
    }
}