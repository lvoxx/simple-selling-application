package com.shitcode.demo1.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shitcode.demo1.annotation.validation.ValidImages;
import com.shitcode.demo1.annotation.validation.ValidVideo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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

        @NotNull
        @Size(max = 255, message = "{validation.product.name.size}")
        @Schema(description = "Name of the product", example = "Wireless Headphones", maxLength = 255, required = true)
        private String name;

        @NotNull
        @Size(max = 3, message = "{validation.product.currency.size}")
        @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.product.currency.pattern}")
        @Schema(description = "Currency code in ISO 4217 format (3 uppercase letters)", example = "USD")
        private String currency;

        @NotNull
        @Positive
        @Schema(description = "Quantity of the product available in stock", example = "100", minimum = "0", required = true)
        private Integer inStockQuantity;

        @NotNull
        @Positive
        @Schema(description = "Quantity of the product currently being sold", example = "50", minimum = "0", required = true)
        private Integer inSellQuantity;

        @NotNull
        @Positive
        @DecimalMin(value = "0.0")
        @Schema(description = "Price of the product", example = "199.99", minimum = "0.01", required = true, format = "double")
        private BigDecimal price;

        @NotNull
        @Positive
        @Schema(description = "Identifier of the category the product belongs to", example = "3", minimum = "1", required = true)
        private Long categoryId;

        @Schema(description = "List of product images (JPEG, PNG only)", type = "array", format = "binary")
        @ValidImages(nullable = false)
        @NotNull
        @JsonIgnore
        private List<MultipartFile> images;

        @Schema(description = "Product video (MP4 only)", format = "binary")
        @ValidVideo(nullable = true)
        @Nullable
        @JsonIgnore
        private MultipartFile video;
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