package com.shitcode.demo1.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shitcode.demo1.utils.DiscountType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

public abstract class DiscountDTO {
    @Data
    @Builder
    @Setter(value = AccessLevel.PRIVATE)
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
    @Builder
    @Setter(value = AccessLevel.PRIVATE)
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
