package com.shitcode.demo1.utils.cache;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum UserCacheType {
    // Products
    @FieldNameConstants.Include
    USERS,
    @FieldNameConstants.Include
    USER_ID,
    @FieldNameConstants.Include
    USER_NAME;
}
