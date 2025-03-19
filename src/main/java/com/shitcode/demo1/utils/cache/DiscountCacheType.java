package com.shitcode.demo1.utils.cache;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum DiscountCacheType {
    // Products
    @FieldNameConstants.Include
    DISCOUNTS_TITLE_EXPDATE,
    @FieldNameConstants.Include
    DISCOUNT_ID,
    @FieldNameConstants.Include
    EXPIRED_DISCOUNTS;
}
