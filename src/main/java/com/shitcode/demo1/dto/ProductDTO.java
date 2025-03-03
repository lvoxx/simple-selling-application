package com.shitcode.demo1.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

public abstract class ProductDTO {

    @Data
    @Builder
    @Setter(value = AccessLevel.PRIVATE)
    @Schema(name = "Product Request", description = "Request payload for creating or updating a product")
    public static class Request {

        @NotBlank(message = "Product name cannot be blank")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        @Schema(description = "Name of the product", example = "Wireless Headphones", maxLength = 255, required = true)
        private String name;

        @Schema(description = "Currency code in ISO 4217 format (3 uppercase letters)", example = "USD")
        @NotBlank(message = "Currency cannot be blank")
        @Size(max = 3, message = "Currency must be exactly 3 characters")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be uppercase and follow ISO 4217 format")
        private String currency;

        @NotNull(message = "In-stock quantity cannot be null")
        @Min(value = 0, message = "In-stock quantity must be at least 0")
        @Schema(description = "Quantity of the product available in stock", example = "100", minimum = "0", required = true)
        private Integer inStockQuantity;

        @NotNull(message = "In-sell quantity cannot be null")
        @Min(value = 0, message = "In-sell quantity must be at least 0")
        @Schema(description = "Quantity of the product currently being sold", example = "50", minimum = "0", required = true)
        private Integer inSellQuantity;

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary value with up to 10 digits and 2 decimal places")
        @Schema(description = "Price of the product", example = "199.99", minimum = "0.01", required = true, format = "double")
        private BigDecimal price;

        @NotNull(message = "Category ID cannot be null")
        @Min(value = 1, message = "Category ID must be greater than 0")
        @Schema(description = "Identifier of the category the product belongs to", example = "3", minimum = "1", required = true)
        private Long categoryId;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Schema(name = "Admin Product Response", description = "Response containing product details for admin users")
    public static class AdminResponse extends AbstractAuditableEntity {

        @Schema(description = "Unique identifier of the product", example = "101")
        private Long id;

        @Schema(description = "Name of the product", example = "Wireless Headphones")
        private String name;

        @Schema(description = "Quantity of the product available in stock", example = "100")
        private Integer inStockQuantity;

        @Schema(description = "Quantity of the product currently being sold", example = "50")
        private Integer inSellQuantity;

        @Schema(description = "Price of the product", example = "199.99", format = "double")
        private Double price;

        @Builder.Default
        @Schema(description = "Currency symbol for the product price", example = "$")
        private String currency = "$";

        @Schema(description = "Category the product belongs to", implementation = CategoryDTO.Response.class, example = "{\"id\": 3, \"name\": \"Electronics\"}")
        private CategoryDTO.Response category;

    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Schema(name = "InSell Product Response", description = "Response containing product details for items available for sale")
    public static class InSellResponse extends AbstractAuditableEntity {

        @Schema(description = "Unique identifier of the product", example = "101")
        private Long id;

        @Schema(description = "Name of the product", example = "Wireless Headphones")
        private String name;

        @Schema(description = "Quantity of the product available for sale", example = "50")
        private Integer availableQuatity;

        @Schema(description = "Price of the product", example = "199.99", format = "double")
        private Double price;

        @Builder.Default
        @Schema(description = "Currency symbol for the product price", example = "$")
        private String currency = "$";

        @Schema(description = "Category the product belongs to", implementation = CategoryDTO.Response.class, example = "{\"id\": 3, \"name\": \"Electronics\"}")
        private CategoryDTO.Response category;

    }
}