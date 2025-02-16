package com.shitcode.demo1.exception.model;

public class WorkerBusyException extends RuntimeException {
    public WorkerBusyException() {
        super();
    }

    public WorkerBusyException(String message) {
        super(message);
    }

    public WorkerBusyException(String message, Throwable throwable) {
        super(message, throwable);
    }
}