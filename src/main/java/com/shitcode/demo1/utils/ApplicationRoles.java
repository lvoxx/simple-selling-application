package com.shitcode.demo1.utils;

import lombok.Getter;

@Getter
public enum ApplicationRoles {
    ADMIN("ADMIN"),
    SUPER_USER("SUPER-USER"),
    USER("USER");

    private final String role;

    ApplicationRoles(String role) {
        this.role = role;
    }
}