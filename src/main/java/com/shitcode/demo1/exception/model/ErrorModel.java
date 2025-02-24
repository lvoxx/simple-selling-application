package com.shitcode.demo1.exception.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

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

    @Schema(description = "The error status associated with the error.", example = "Not Found")
    private HttpStatus status;

    @Schema(description = "List of detailed error messages.", example = " [\r\n" + //
            "    {\r\n" + //
            "      \"field\": \"email\",\r\n" + //
            "      \"message\": \"Email is required\"\r\n" + //
            "    },\r\n" + //
            "    {\r\n" + //
            "      \"field\": \"password\",\r\n" + //
            "      \"message\": \"Password must be at least 8 characters\"\r\n" + //
            "    }\r\n" + //
            "  ]")
    private List<FieldError> errors;

    @Schema(description = "The timestamp when the error occurred.", example = "2024-10-24 14:35:00:000 GTM+7")
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS 'GMT'XXX")
    @Builder.Default
    private ZonedDateTime time = ZonedDateTime.now(ZoneId.systemDefault());

    @Data
    @Builder
    @Schema(description = "Details of a specific field validation error.")
    public static class FieldError {
        @Schema(description = "The name of the field that caused the error.", example = "email")
        private String field;

        @Schema(description = "The validation error message.", example = "Email is required")
        private String message;
    }

    public static ErrorModel of(HttpStatus status, String message, List<FieldError> errors) {
        return ErrorModel.builder()
                .time(ZonedDateTime.now())
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }
}
