package com.shitcode.demo1.utils;

public enum KeyLock {
    CATEGORY("category"),
    PRODUCT("product"),
    USER("user");

    private final String key;

    KeyLock(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
