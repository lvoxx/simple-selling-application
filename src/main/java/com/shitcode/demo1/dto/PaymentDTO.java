package com.shitcode.demo1.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
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

        private UUID discountId;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Schema(name = "Payment Response", description = "Response containing Recipe and PayPal transaction data")
    public static class Response extends AbstractAuditableEntity {
        private Long id;
        private String name;
        private String username;
        private String shippingAddress;
        private String shippingFee;
        private String description;
        private Double total;
        private List<ProductWithQuantityResponse> products;
        private List<PaypalTransactionResponse> transactions;
        private UUID discountId;
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
        private Long id;
        private String name;
        private Double price;
        private Integer quantity;
        private Double subTotal;
    }

    @Data
    @Builder
    public static class PaypalTransactionResponse {
        private String id;
        private String status;
        private String paymentMethod;
        private Double amount;
    }
}