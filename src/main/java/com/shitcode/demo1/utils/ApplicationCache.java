package com.shitcode.demo1.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public abstract class ApplicationCache {
    private static final Map<String, Cache<Object, Object>> caches = new LinkedHashMap<>();

    public static Map<String, Cache<Object, Object>> getCaches() {
        // * USER CACHE
        caches.put("users",
                buildCache(100, 200, 15, TimeUnit.MINUTES));
        // * CATEGORY CACHE
        caches.put("categories",
                buildCache(10, 50, 30, TimeUnit.DAYS));
        caches.put("category-id",
                buildCache(20, 100, 1, TimeUnit.HOURS));
        caches.put("category-name",
                buildCache(20, 100, 1, TimeUnit.HOURS));
        // * PRODUCT
        caches.put("admin-products",
                buildCache(900, 1500, 2, TimeUnit.MINUTES));
        caches.put("insell-products",
                buildCache(900, 1500, 2, TimeUnit.MINUTES));
        // ? NAME
        caches.put("admin-products-name",
                buildCache(100, 400, 2, TimeUnit.DAYS));
        caches.put("insell-products-name",
                buildCache(400, 1200, 12, TimeUnit.HOURS));
        // ? ID
        caches.put("admin-products-id",
                buildCache(100, 600, 7, TimeUnit.DAYS));
        caches.put("insell-products-id",
                buildCache(500, 2500, 12, TimeUnit.HOURS));

        return caches;
    }

    private static Cache<Object, Object> buildCache(
            int initialCapacity, int maximumSize, int duration, TimeUnit unit) {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterAccess(duration, unit)
                .recordStats()
                .build();
    }

}