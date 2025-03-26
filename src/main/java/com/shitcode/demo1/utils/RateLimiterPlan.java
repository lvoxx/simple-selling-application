package com.shitcode.demo1.utils;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;

/**
 * Enum representing different rate limiter plans, each with a predefined
 * bandwidth limit and refill interval. Each plan specifies a maximum
 * number of requests allowed and the duration for refilling the limit.
 */
public enum RateLimiterPlan {

    /**
     * Basic plan allowing 15 requests every 2 minutes.
     */
    BASIC {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(15)
                    .refillIntervally(15, Duration.ofMinutes(2))
                    .build();
        }
    },

    /**
     * Maximum plan allowing 1000 requests every 1 hour.
     */
    MAX {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(1000)
                    .refillIntervally(1000, Duration.ofHours(1))
                    .build();
        }
    },

    /**
     * Plan for heavy loads allowing 55 requests every 5 minutes.
     */
    HEAVY_LOADS {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(55)
                    .refillIntervally(55, Duration.ofMinutes(5))
                    .build();
        }
    },

    /**
     * Soft limit plan allowing 30 requests every 5 minutes.
     */
    SOFT {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(30)
                    .refillIntervally(30, Duration.ofMinutes(5))
                    .build();
        }
    },

    /**
     * Medium limit plan allowing 25 requests every 5 minutes.
     */
    MEDIUM {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(25)
                    .refillIntervally(25, Duration.ofMinutes(5))
                    .build();
        }
    },

    /**
     * Hard limit plan allowing 12 requests every 10 minutes.
     */
    HARD {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(12)
                    .refillIntervally(12, Duration.ofMinutes(10))
                    .build();
        }
    },

    /**
     * Authentication plan allowing 5 requests every 30 minutes.
     */
    AUTH {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofMinutes(30))
                    .build();
        }
    },

    /**
     * Plan for completing sign-up process allowing 5 requests every 7 days.
     */
    COMPLETE_SIGNUP {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofDays(7))
                    .build();
        }
    },

    /**
     * Sign-up plan allowing 2 requests every 7 days.
     */
    SIGNUP {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(2)
                    .refillIntervally(2, Duration.ofDays(7))
                    .build();
        }
    },

    /**
     * Plan for regranting authentication allowing 3 requests every 45 minutes.
     */
    REGRANT_AUTH {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(3)
                    .refillIntervally(3, Duration.ofMinutes(45))
                    .build();
        }
    },

    /**
     * Token renewal plan allowing 5 requests every 30 days.
     */
    RENEW_TOKEN {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofDays(30))
                    .build();
        }
    },

    /**
     * Product interaction plan allowing 20 requests every 5 minutes.
     */
    PRODUCT_INTERACTION {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(20)
                    .refillIntervally(20, Duration.ofMinutes(5))
                    .build();
        }
    };

    /**
     * Returns the bandwidth limit associated with the rate limiter plan.
     * 
     * @return the {@link Bandwidth} object containing the capacity and refill
     *         interval.
     */
    public abstract Bandwidth getLimit();
}
