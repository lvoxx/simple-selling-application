package com.shitcode.demo1.utils;

public enum KeyLock {
    CATEGORY("CATEGORY"),
    PRODUCT("PRODUCT"),
    DISCOUNT("DISCOUNT"),
    USER("USER");

    private final String key;

    KeyLock(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
