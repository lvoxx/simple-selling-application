package com.shitcode.demo1.utils;

import lombok.Getter;

@Getter
public enum ApplicationRoles {
    ADMIN("admin"),
    SUPER_USER("super-user"),
    USER("user");

    private final String role;

    ApplicationRoles(String role) {
        this.role = role;
    }
}