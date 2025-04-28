package com.shitcode.demo1.repository.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDiscountDTO {

    private Long categoryId;
    private String categoryName;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String currency;
    private Integer quantity;
    private UUID discountId;
    private String discountTitle;
    private String discountType;
    private Double salesPercentAmount;
    private Instant discountExpDate;

}
