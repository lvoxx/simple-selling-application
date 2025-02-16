package com.shitcode.demo1.exception.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model representing an error response.")
public class ErrorModel {

    @Schema(description = "The error message detailing what went wrong.", example = "Resource not found")
    private String message;

    @Schema(description = "The error code associated with the error.", example = "404")
    private Integer code;

    @Schema(description = "The context path where the error occurred.", example = "/api/auth/login")
    private String contextPath;

    @Schema(description = "List of detailed error messages.", example = "[\"Invalid ID format\", \"Missing required field\"]")
    private List<String> errors;

    @Schema(description = "The timestamp when the error occurred.", example = "2024-10-24 14:35:00:000 GTM+7")
    private String time;
}
