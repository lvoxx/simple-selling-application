package com.shitcode.demo1.exception.model;

public class SendingMailException extends RuntimeException {
    public SendingMailException() {
        super();
    }

    public SendingMailException(String message) {
        super(message);
    }

    public SendingMailException(String message, Throwable throwable) {
        super(message, throwable);
    }
}