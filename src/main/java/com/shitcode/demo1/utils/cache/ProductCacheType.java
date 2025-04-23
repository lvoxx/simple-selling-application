package com.shitcode.demo1.utils.cache;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum ProductCacheType {
    // Products
    @FieldNameConstants.Include
    INSELL_PRODUCTS,
    @FieldNameConstants.Include
    ADMIN_PRODUCTS,
    @FieldNameConstants.Include
    ADMIN_PRODUCT_ID,
    @FieldNameConstants.Include
    INSELL_PRODUCT_ID,
    @FieldNameConstants.Include
    ADMIN_PRODUCT_NAME,
    @FieldNameConstants.Include
    INSELL_PRODUCT_NAME,
    @FieldNameConstants.Include
    PAYMENT_PRODUCTS;
}
