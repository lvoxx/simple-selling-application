package com.shitcode.demo1.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class ProductInteractionDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter(value = AccessLevel.PRIVATE)
    @Schema(name = "ProductInteractionRequest", description = "Represents a request to interact with a product.")
    public static class Request {
        @Schema(description = "ID of the product being interacted with", example = "69", required = false) // nullable =
                                                                                                           // true is
                                                                                                           // deprecated
        private Long productId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ProductInteractionResponse", description = "Represents the rate limits for product interactions.")
    public static class Response {
        @Schema(description = "Unique identifier for the rate limit entry", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;

        @Schema(description = "ID of the product associated with the interaction", example = "69")
        private Long productId;

        @Schema(description = "Username of the user who interacted with the product", example = "john_doe")
        private String username;

        @Schema(description = "Timestamp of the interaction", example = "2024-03-24T12:30:45")
        private LocalDateTime onTime;

        @Schema(description = "Location where the interaction occurred", example = "New York, USA")
        private String locateAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ProductInteractionResponse", description = "Represents the rate limits for product interactions.")
    public static class PageResponse {
        private Long productId;
        private String productName; 
        private String categoryName;
        private String locateAt; 
        private LocalDateTime onTime;
    }   
}
