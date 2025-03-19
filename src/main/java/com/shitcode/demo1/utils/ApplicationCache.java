package com.shitcode.demo1.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shitcode.demo1.utils.cache.CategoryCacheType;
import com.shitcode.demo1.utils.cache.DiscountCacheType;
import com.shitcode.demo1.utils.cache.ProductCacheType;
import com.shitcode.demo1.utils.cache.UserCacheType;

/**
 * A cache manager for storing various application-related caches.
 * 
 * <p>
 * This class provides a centralized cache configuration using Caffeine.
 * It maintains caches for different data categories, including Users,
 * Categories, Products, and Discounts, with defined expiration policies.
 * </p>
 */
public abstract class ApplicationCache {
        private static final Map<String, Cache<Object, Object>> caches = new LinkedHashMap<>();

        /**
         * Initializes and returns a map of predefined caches for different application
         * entities.
         * <p>
         * The caches are categorized as follows:
         * <ul>
         * <li><b>User Cache</b>: Stores user-related data with different expiration
         * times.</li>
         * <li><b>Category Cache</b>: Maintains category-related details with
         * short-lived expiration.</li>
         * <li><b>Product Cache</b>: Caches product-related data, including names and
         * IDs.</li>
         * <li><b>Discount Cache</b>: Holds discount information with varying expiration
         * times.</li>
         * </ul>
         * </p>
         * 
         * @return A map containing various named caches.
         */
        public static Map<String, Cache<Object, Object>> getCaches() {
                // * USER CACHE
                caches.put(UserCacheType.Fields.USERS,
                                buildCache(100, 200, 15, TimeUnit.MINUTES));
                caches.put(UserCacheType.Fields.USER_ID,
                                buildCache(200, 1400, 180, TimeUnit.DAYS));// 6 months
                caches.put(UserCacheType.Fields.USER_NAME,
                                buildCache(400, 1200, 180, TimeUnit.DAYS));// 6 months
                // * CATEGORY CACHE
                caches.put(CategoryCacheType.Fields.CATEGORIES,
                                buildCache(10, 50, 30, TimeUnit.DAYS));
                caches.put(CategoryCacheType.Fields.CATEGORY_ID,
                                buildCache(20, 100, 1, TimeUnit.HOURS));
                caches.put(CategoryCacheType.Fields.CATEGORY_NAME,
                                buildCache(20, 100, 1, TimeUnit.HOURS));
                // * PRODUCT
                caches.put(ProductCacheType.Fields.ADMIN_PRODUCTS,
                                buildCache(900, 1500, 2, TimeUnit.MINUTES));
                caches.put(ProductCacheType.Fields.INSELL_PRODUCTS,
                                buildCache(900, 1500, 2, TimeUnit.MINUTES));
                // ? PRODUCT-NAME
                caches.put(ProductCacheType.Fields.ADMIN_PRODUCT_NAME,
                                buildCache(100, 400, 2, TimeUnit.DAYS));
                caches.put(ProductCacheType.Fields.INSELL_PRODUCT_NAME,
                                buildCache(400, 1200, 12, TimeUnit.HOURS));
                // ? PRODUCT-ID
                caches.put(ProductCacheType.Fields.ADMIN_PRODUCT_ID,
                                buildCache(100, 600, 7, TimeUnit.DAYS));
                caches.put(ProductCacheType.Fields.INSELL_PRODUCT_ID,
                                buildCache(500, 2500, 12, TimeUnit.HOURS));
                // * DISCOUNT
                caches.put(DiscountCacheType.Fields.DISCOUNTS_TITLE_EXPDATE,
                                buildCache(50, 150, 1, TimeUnit.HOURS));
                caches.put(DiscountCacheType.Fields.DISCOUNT_ID,
                                buildCache(20, 120, 2, TimeUnit.HOURS));
                caches.put(DiscountCacheType.Fields.EXPIRED_DISCOUNTS,
                                buildCache(50, 200, 24, TimeUnit.HOURS));

                return caches;
        }

        /**
         * Builds and configures a cache with the specified parameters.
         * 
         * @param initialCapacity The initial capacity of the cache.
         * @param maximumSize     The maximum number of items the cache can hold.
         * @param duration        The expiration duration after the last access.
         * @param unit            The time unit for the expiration duration.
         * @return A configured Caffeine cache instance.
         */
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