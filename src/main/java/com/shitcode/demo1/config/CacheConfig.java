package com.shitcode.demo1.config;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shitcode.demo1.utils.ApplicationCache;
import com.shitcode.demo1.utils.LogPrinter;

@Configuration
@EnableCaching
public class CacheConfig {
        @Bean
        CacheManager cacheManager() {
                CaffeineCacheManager cacheManager = new CaffeineCacheManager();
                for (Map.Entry<String, Cache<Object, Object>> cache : ApplicationCache.getCaches().entrySet()) {
                        cacheManager.registerCustomCache(cache.getKey(), cache.getValue());
                        LogPrinter.printLog(LogPrinter.Type.INFO, LogPrinter.Flag.START_UP,
                                        "Created cache with name  " + cache.getKey());
                }

                return cacheManager;
        }

        @Bean
        Caffeine<Object, Object> caffeineSpec() {
                return Caffeine.newBuilder()
                                .initialCapacity(10)
                                .maximumSize(500)
                                .expireAfterAccess(30, TimeUnit.MINUTES);
        }

}
