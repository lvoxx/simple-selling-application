package com.shitcode.demo1.utils;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum KeyLock {
    @FieldNameConstants.Include
    CATEGORY,
    @FieldNameConstants.Include
    PRODUCT,
    @FieldNameConstants.Include
    IMAGE,
    @FieldNameConstants.Include
    VIDEO,
    @FieldNameConstants.Include
    DISCOUNT,
    @FieldNameConstants.Include
    USER;
}
