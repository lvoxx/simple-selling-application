package com.shitcode.demo1.component;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.function.ThrowingSupplier;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shitcode.demo1.annotation.spring.LazyComponent;
import com.shitcode.demo1.exception.model.KeyLockMissedException;
import com.shitcode.demo1.exception.model.WorkerBusyException;
import com.shitcode.demo1.utils.KeyLock;
import com.shitcode.demo1.utils.LogPrinter;

import jakarta.annotation.PostConstruct;

@LazyComponent
public class DatabaseLock {
    private static final Logger log = LoggerFactory.getLogger(DatabaseLock.class);
    private static final Integer TRY_TIMES = 6;
    private static final Integer RETRY_PERIOD = 10000; // 10s

    private Cache<String, Boolean> cache;

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // Auto-expire locks after 5 minutes
                .maximumSize(1000)
                .build();
    }

    public boolean tryLock(String key) {
        // Attempt to put a lock if absent
        return cache.asMap().putIfAbsent(key, true) == null;
    }

    public void releaseLock(String key) {
        cache.invalidate(key);
    }

    public boolean isLocked(String key) {
        return cache.getIfPresent(key) != null;
    }

    // Generic method for Supplier<T>
    public <T> T doAndLock(KeyLock key, Object id, ThrowingSupplier<T> task) {
        int retriesLeft = TRY_TIMES;
        String keyName = key.getKey().concat(id.toString());
        while (!tryLock(keyName)) {
            log.debug("[DEBUG] Key '{}' is already locked. Retrying in {} ms...", keyName, RETRY_PERIOD);
            if (--retriesLeft == 0) {
                log.warn("[WARN] - Could not acquire lock for key '{}' after {} attempts.", keyName, TRY_TIMES);
                throw new WorkerBusyException("{exception.worker-busy.db-lock}");
            }
            try {
                Thread.sleep(RETRY_PERIOD);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[ERROR] - Thread interrupted while waiting for lock on key '{}'", keyName, e);
                throw new WorkerBusyException("{exception.worker-busy.thread}");
            }
        }

        try {
            return task.get();
        } catch (Exception e) {
            String message = String.format("{exception.key-lock.missed}", keyName);
            LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.UTILS_FLAG, message);
            throw new KeyLockMissedException(message, e);
        } finally {
            releaseLock(keyName);
        }
    }
}
