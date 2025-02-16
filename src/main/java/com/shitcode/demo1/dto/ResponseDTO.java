package com.shitcode.demo1.dto;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "ResponseDTO", description = "Standard API response structure")
public class ResponseDTO {

    @Schema(description = "The API endpoint that was accessed", example = "/api/v1/products")
    private String path;

    @Schema(description = "Timestamp when the request was received", example = "2024-10-13 14:55:30 GTM+7")
    private String requestTime;

    @Schema(description = "The actual response data returned by the API")
    private Object data;

    @Schema(description = "Identifier of the user who made the request", example = "Annonymous User")
    private String requester;

    @Schema(description = "Information regarding rate limits for the requester")
    private RateLimits rateLimits;

    @Schema(description = "Details about the response status and processing time")
    private Transper transper;

    @Data
    @Builder
    @Schema(name = "RateLimits", description = "Rate limiting information for the requester")
    public static class RateLimits {

        @Schema(description = "Total number of requests allowed within the current time window", example = "1000")
        private long total;

        @Schema(description = "Number of remaining requests that can be made in the current window", example = "750")
        private long remaining;

        @Schema(description = "Time when the rate limit will reset", example = "00:05:00")
        private String resetAfter;
    }

    @Data
    @Builder
    @Schema(name = "Transper", description = "Information about the HTTP response and processing time")
    public static class Transper {

        @Schema(description = "HTTP status code of the response", example = "200", implementation = HttpStatus.class)
        private int statusCode;

        @Schema(description = "Time taken to process the request, in milliseconds", example = "123")
        private long processingTimeMs;
    }
}