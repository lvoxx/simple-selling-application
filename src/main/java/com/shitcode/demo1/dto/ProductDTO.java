package com.shitcode.demo1.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public abstract class ProductDTO {

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Schema(name = "Product Create Request", description = "Request payload for creating or updating a product")
    public static class CreateRequest {

        @NotBlank(message = "{validation.product.name.blank}")
        @Size(max = 255, message = "{validation.product.name.size}")
        @Schema(description = "Name of the product", example = "Wireless Headphones", maxLength = 255, required = true)
        private String name;

        @NotBlank(message = "{validation.product.currency.blank}")
        @Size(max = 3, message = "{validation.product.currency.size}")
        @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.product.currency.pattern}")
        @Schema(description = "Currency code in ISO 4217 format (3 uppercase letters)", example = "USD")
        private String currency;

        @NotNull(message = "{validation.product.in-stock-quantity.not-null}")
        @Positive(message = "{validation.product.in-stock-quantity.positive}")
        @Schema(description = "Quantity of the product available in stock", example = "100", minimum = "0", required = true)
        @JsonProperty("in-stock-quantity")
        private Integer inStockQuantity;

        @NotNull(message = "{validation.product.in-sell-quantity.not-null}")
        @Positive(message = "{validation.product.in-sell-quantity.positive}")
        @Schema(description = "Quantity of the product currently being sold", example = "50", minimum = "0", required = true)
        @JsonProperty("in-sell-quantity")
        private Integer inSellQuantity;

        @NotNull(message = "{validation.product.price.not-null}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{validation.product.price.min}")
        @Schema(description = "Price of the product", example = "199.99", minimum = "0.01", required = true, format = "double")
        private BigDecimal price;

        @NotNull(message = "{validation.product.category-id.not-null}")
        @Positive(message = "{validation.product.category-id.positive}")
        @Schema(description = "Identifier of the category the product belongs to", example = "3", minimum = "1", required = true)
        @JsonProperty("category-id")
        private Long categoryId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "Product Update Request", description = "Request payload for creating or updating a product")
    public static class UpdateRequest extends CreateRequest {
        // 'http://localhost:9090/...jpg': 'New Image Name.jpg'
        @JsonProperty("old-image-urls-to-new-image-file-names")
        private Map<String, String> updateOldImageUrlToNewImageFileName;
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

        @Schema(description = "List of image URLs associated with the product", example = "[\"https://example.com/images/product1.jpg\", \"https://example.com/images/product2.jpg\"]")
        private List<String> imageUrls;

        @Schema(description = "Video URL showcasing the product", example = "https://example.com/videos/product.mp4")
        private String videoUrl;
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

        @Schema(description = "List of image URLs associated with the product", example = "[\"https://example.com/images/product1.jpg\", \"https://example.com/images/product2.jpg\"]")
        private List<String> imageUrls;

        @Schema(description = "Video URL showcasing the product", example = "https://example.com/videos/product.mp4")
        private String videoUrl;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Schema(name = "With Discount Product Response", description = "Response containing product details for sub-discount when finding by discount id")
    public static class DiscountResponse extends AbstractAuditableEntity {

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

    }

}