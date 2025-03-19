package com.shitcode.demo1.scheduler;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shitcode.demo1.service.DiscountService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Type;
import com.shitcode.demo1.utils.cache.DiscountCacheType;

import jakarta.annotation.PostConstruct;

/**
 * Scheduler for checking and removing expired discounts from products.
 * <p>
 * This scheduler retrieves expired discounts from the cache and removes them
 * from the products at the start of every minute.
 * </p>
 */
@Component
public class CheckingExpiredDiscountScheduler {
    private final DiscountService discountService;
    private final CacheManager cacheManager;

    public CheckingExpiredDiscountScheduler(DiscountService discountService, CacheManager cacheManager) {
        this.discountService = discountService;
        this.cacheManager = cacheManager;
    }

    private Set<Object> discountIds;
    private org.springframework.cache.Cache cache;

    /**
     * Initializes the cache and retrieves expired discount IDs after bean
     * construction.
     */
    @PostConstruct
    void setUp() {
        cache = cacheManager.getCache(DiscountCacheType.Fields.EXPIRED_DISCOUNTS);
        if (cache != null && cache instanceof CaffeineCache) {
            // Get the native Caffeine cache
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = ((CaffeineCache) cache)
                    .getNativeCache();
            // Return all keys as a Set
            discountIds = nativeCache.asMap().keySet();
        }
    }

    /**
     * Scheduled task to remove expired discounts from products.
     * <p>
     * This job runs at the beginning of every minute (e.g., 12:00:00, 12:01:00,
     * etc.)
     * to check if any discounts have expired and remove them from associated
     * products.
     * </p>
     */
    @Scheduled(cron = "0 * * * * *") // Runs at 00 seconds of every minute in system
    void removeExpiredDiscounts() {
        if (discountIds.size() == 0) {
            LogPrinter.printSchedulerLog(Type.SCHEDULER, "CheckingExpiredDiscountScheduler", "removeExpiredDiscounts",
                    Instant.now().toString(),
                    "No expired discounts found. Skip this job.");
            return;
        }
        AtomicInteger numberOfExpiringDiscounts = new AtomicInteger(0);
        OffsetDateTime timeNow = OffsetDateTime.now();
        discountIds.forEach(id -> {
            Optional<Object> idValue = Optional.ofNullable(cache.get(id));
            if (!idValue.isPresent()) {
                return;
            }

            // Parse expiration datetime from cached value
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            OffsetDateTime expDatetime = OffsetDateTime.parse(idValue.get().toString(), formatter);

            // If the discount has expired, remove it from products
            if (timeNow.compareTo(expDatetime) >= 0) {
                discountService.removeExpiredDiscountsFromProducts(UUID.fromString(id.toString()));
                numberOfExpiringDiscounts.incrementAndGet();
            }
        });

        // Log the number of removed expired discounts
        LogPrinter.printSchedulerLog(Type.SCHEDULER, "CheckingExpiredDiscountScheduler", "removeExpiredDiscounts",
                Instant.now().toString(),
                "Run scheduler job to remove expired discount from products with "
                        .concat(String.valueOf(numberOfExpiringDiscounts.get())).concat(" times."));
    }
}
