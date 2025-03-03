package com.shitcode.demo1.dto;

import com.shitcode.demo1.utils.InteractionEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

public abstract class ProductInteractionDTO {

    @Data
    @Builder
    @Setter(value = AccessLevel.PRIVATE)
    @Schema(name = "Product Interaction Request", description = "Represents a request to interact with a product.")
    public static class Request {
        @Schema(description = "Username of the user interacting with the product", example = "john_doe")
        private String username;

        @Schema(description = "Name of the product being interacted with", example = "Gaming Laptop", nullable = true)
        private String productName;

        @Schema(description = "Name of the category being interacted with", example = "Computer", nullable = true)
        private String categoryName;

        @Schema(description = "Type of interaction event performed on the product or category", example = "view_and_buy", allowableValues = {
                "pick_category", "search_product", "view_product", "view_and_add_to_cart",
                "view_and_buy", "cart_to_purchase", "pay_by_card", "pay_by_paypal", "pay_by_cash"
        })
        private InteractionEvent event;
    }
}
