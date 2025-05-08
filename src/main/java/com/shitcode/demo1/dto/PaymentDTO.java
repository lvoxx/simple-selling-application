package com.shitcode.demo1.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shitcode.demo1.entity.Recipe;
import com.shitcode.demo1.utils.DiscountType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public abstract class PaymentDTO {
    @Data
    @Builder
    @Schema(name = "Payment Request", description = "Request payload for creating a recipe")
    public static class Request {
        @NotBlank(message = "{payment.name.blank}")
        @Size(max = 100, message = "{payment.name.size}")
        private String name;

        @NotBlank(message = "{payment.description.blank}")
        @Size(max = 1000, message = "{payment.description.size}")
        private String description;

        @NotEmpty(message = "{payment.product.empty}")
        private List<ProductQuantityDTO> products;

        @NotNull(message = "{payment.shipping-address.null}")
        @Size(max = 300, message = "{payment.shipping-address.size}")
        private String shippingAddress;

        @NotNull(message = "{payment.shipping-fee.null}")
        @DecimalMin(value = "0", message = "{payment.shipping-fee.min}")
        private Double shippingFee;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Schema(name = "Payment Response", description = "Response containing Recipe and PayPal transaction data")
    public static class Response extends AbstractAuditableEntity {
        private UUID recipeId;
        private String name;
        private Recipe.RecipeStatus status;
        private String description;
        private String redirectToPayoutUrl;
        private Double total;
        private String username;
        private String shippingAddress;
        private String shippingFee;
        private List<ProductWithQuantityResponse> recipeProducts;
        private PaypalTransactionResponse paypalTransaction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductQuantityDTO {

        @NotNull(message = "{payment.product.null-id}")
        private Long productId;

        @NotNull(message = "{payment.product.null-quantity}")
        @Min(value = 1, message = "{payment.product.min-quantity}")
        private Integer quantity;
    }

    @Data
    @Builder
    public static class ProductWithQuantityResponse {
        private String categoryName;
        private String productName;
        private Integer quantity;
        private Double price;
        private String discountName;
        private DiscountType discountType;
        private Double discountAmount;
        private Double subTotal;
    }

    @Data
    @Builder
    public static class PaypalTransactionResponse {
        private String transactionId;
        private Double amount;
        private LocalDateTime transactionDate;
    }
}