package com.shitcode.demo1.utils.cache;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum Ip2LocationCacheType {
    @FieldNameConstants.Include
    IP_ADDRESS;
}
