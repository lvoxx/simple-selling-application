package com.shitcode.demo1.exception.model;

public class KeyLockMissedException extends RuntimeException {
    public KeyLockMissedException() {
        super();
    }

    public KeyLockMissedException(String message) {
        super(message);
    }

    public KeyLockMissedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}