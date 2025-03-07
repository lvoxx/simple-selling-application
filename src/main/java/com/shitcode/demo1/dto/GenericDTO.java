package com.shitcode.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class GenericDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "GenericRequest", description = "Request containing Generic Data")
    public static class Request {
        @NotNull(message = "Request cannot be null")
        @Schema(description = "The request payload", example = "{request: {some-thing: 1, some-data: \"Yae Milo\"}}")
        private Object request;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @Schema(name = "GenericResponse", description = "Response containing Generic Data")
    public static class Response {
        @Schema(description = "Response message", example = "Success")
        private final String message;
    }
}