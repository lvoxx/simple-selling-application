package com.shitcode.demo1.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shitcode.demo1.utils.DiscountType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

public abstract class DiscountDTO {
    @Data
    @Builder
    public static class ManageRequest {
        private DiscountType type;
        private Double salesPercentAmount;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    public static class ManageResponse extends AbstractAuditableEntity {
        private UUID id;
        private String title;
        private DiscountType type;
        private Double salesPercentAmount;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private OffsetDateTime expDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    public static class DiscountDetailsResponse extends AbstractAuditableEntity {
        private UUID id;
        private String title;
        private DiscountType type;
        private Double salesPercentAmount;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private OffsetDateTime expDate;
        private List<ProductDTO.DiscountResponse> products;
    }

    @Data
    @Builder
    public static class ApplyToProductsRequest {
        private List<Long> productIds;
        private UUID discountId;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    public static class ApplyToProductsResponse extends AbstractAuditableEntity {
        private List<Long> productIds;
        private UUID discountId;
    }
}
