package com.shitcode.demo1.dto;

import com.shitcode.demo1.annotation.validation.Email;
import com.shitcode.demo1.annotation.validation.Password;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

public abstract class AuthDTO {
    @Data
    @Builder
    @Schema(name = "Auth Request", description = "Request payload for user login")
    public static class Request {
        @Email(message = "{validation.auth.email}")
        @Schema(description = "User email address", example = "user@example.com", required = true)
        private final String email;
        @Password(message = "{validation.auth.password}", minSize = 6, maxSize = 30, containsSpecialChar = false)
        @Schema(description = "User password", example = "P@ssw0rd!", required = true)
        private final String password;
    }

    @Data
    @Builder
    @Setter(value = AccessLevel.PRIVATE)
    @Schema(name = "Auth Response", description = "Response containing JWT tokens")
    public static class Response {
        @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR...")
        private String accessToken;
        @Schema(description = "JWT Refresh Token", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...")
        private String refreshToken;
    }

}