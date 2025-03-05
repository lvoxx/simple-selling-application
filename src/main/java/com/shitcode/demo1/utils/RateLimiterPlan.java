package com.shitcode.demo1.utils;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;

public enum RateLimiterPlan {

    BASIC {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(15)
                    .refillIntervally(15, Duration.ofMinutes(2))
                    .build();
        }
    },
    MAX {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(1000)
                    .refillIntervally(1000, Duration.ofHours(1))
                    .build();
        }
    },
    HEAVY_LOADS {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(55)
                    .refillIntervally(55, Duration.ofMinutes(5))
                    .build();
        }
    },
    SOFT {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(30)
                    .refillIntervally(30, Duration.ofMinutes(5))
                    .build();
        }
    },
    MEDIUM {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(25)
                    .refillIntervally(25, Duration.ofMinutes(5))
                    .build();
        }
    },
    HARD {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(12)
                    .refillIntervally(12, Duration.ofMinutes(10))
                    .build();
        }
    },
    AUTH {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofMinutes(30))
                    .build();
        }
    },
    SIGNUP {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(2)
                    .refillIntervally(2, Duration.ofDays(7))
                    .build();
        }
    },
    REGRANT_AUTH {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(3)
                    .refillIntervally(3, Duration.ofMinutes(45))
                    .build();
        }
    },
    RENEW_TOKEN {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofDays(30))
                    .build();
        }
    };

    // Abstract method that each constant must implement
    public abstract Bandwidth getLimit();
}