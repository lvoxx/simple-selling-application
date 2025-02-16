package com.shitcode.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

public abstract class CategoryDTO {
    @Data
    @Builder
    @Setter(value = AccessLevel.PRIVATE)
    @Schema(name = "Category Request", description = "Request payload for category management")
    public static class Request {
        @NotBlank(message = "Category name cannot be blank")
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        @Schema(description = "Name of the category", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Schema(name = "Category Response", description = "Response containing Category Data")
    public static class Response extends AbstractAuditableEntity {
        @Schema(description = "Category Id", example = "1L")
        private Long id;
        @Schema(description = "Category Name", example = "Vsmart")
        private String name;
    }
}