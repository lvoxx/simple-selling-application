package com.shitcode.demo1.utils.cache;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum ProductInterationCacheType {
    @FieldNameConstants.Include
    TIME_BETWEEN;
}
