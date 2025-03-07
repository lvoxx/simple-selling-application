package com.shitcode.demo1.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "{validation.product.name.blank}")
        @Size(max = 255, message = "{validation.product.name.size}")
        @Schema(description = "Name of the product", example = "Wireless Headphones", maxLength = 255, required = true)
        private String name;

        @NotBlank(message = "{validation.product.currency.blank}")
        @Size(max = 3, message = "{validation.product.currency.size}")
        @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.product.currency.pattern}")
        @Schema(description = "Currency code in ISO 4217 format (3 uppercase letters)", example = "USD")
        private String currency;

        @NotBlank(message = "{validation.product.in-stock-quantity.blank}")
        @Min(value = 0, message = "{validation.product.in-stock-quantity.min}")
        @Schema(description = "Quantity of the product available in stock", example = "100", minimum = "0", required = true)
        private Integer inStockQuantity;

        @NotBlank(message = "{validation.product.in-sell-quantity.blank}")
        @Min(value = 0, message = "{validation.product.in-sell-quantity.min}")
        @Schema(description = "Quantity of the product currently being sold", example = "50", minimum = "0", required = true)
        private Integer inSellQuantity;

        @NotBlank(message = "{validation.product.price.blank}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{validation.product.price.min}")
        @Digits(integer = 99, fraction = 2, message = "{validation.product.price.digits}")
        @Schema(description = "Price of the product", example = "199.99", minimum = "0.01", required = true, format = "double")
        private BigDecimal price;

        @NotBlank(message = "{validation.product.category-id.blank}")
        @Min(value = 1, message = "{validation.product.category-id.min}")
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