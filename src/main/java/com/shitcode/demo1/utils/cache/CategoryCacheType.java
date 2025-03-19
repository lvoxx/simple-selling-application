package com.shitcode.demo1.utils.cache;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum CategoryCacheType {
    // Products
    @FieldNameConstants.Include
    CATEGORIES,
    @FieldNameConstants.Include
    CATEGORY_ID,
    @FieldNameConstants.Include
    CATEGORY_NAME;
}
