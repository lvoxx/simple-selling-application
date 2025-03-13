package com.shitcode.demo1.scheduler;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shitcode.demo1.service.DiscountService;

import jakarta.annotation.PostConstruct;

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

    @PostConstruct
    void setUp() {
        cache = cacheManager.getCache("expired-discount");
        if (cache != null && cache instanceof CaffeineCache) {
            // Get the native Caffeine cache
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = ((CaffeineCache) cache)
                    .getNativeCache();
            // Return all keys as a Set
            discountIds = nativeCache.asMap().keySet();
        }
    }

    /**
     * Runs at the start of every minute (e.g., 12:00:00, 12:01:00, etc.)
     */
    @Scheduled(cron = "0 * * * * *") // Runs at 00 seconds of every minute
    void removeExpiredDiscounts() {
        OffsetDateTime timeNow = OffsetDateTime.now();
        discountIds.forEach(id -> {
            Optional<Object> idValue = Optional.ofNullable(cache.get(id));
            if (!idValue.isPresent()) {
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            OffsetDateTime expDatetime = OffsetDateTime.parse(idValue.get().toString(), formatter);
            if (timeNow.compareTo(expDatetime) >= 0) {
                discountService.removeExpiredDiscountsFromProducts(UUID.fromString(id.toString()));
            }
        });
    }
}
