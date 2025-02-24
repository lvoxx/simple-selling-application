package com.shitcode.demo1.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "ResponseDTO", description = "Standard API response structure")
public class ResponseDTO {

    @Schema(description = "The actual response data returned by the API")
    private Object data;

    @Schema(description = "Timestamp when the request was received", example = "2024-10-13 14:55:30 GTM+7")
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS 'GMT'XXX")
    @Builder.Default
    private ZonedDateTime requestTime = ZonedDateTime.now(ZoneId.systemDefault());

    @Schema(description = "HTTP status of the response", example = "OK")
    private HttpStatusCode status;

    @Schema(description = "Information regarding rate limits for the requester")
    private RateLimits rateLimits;

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
}