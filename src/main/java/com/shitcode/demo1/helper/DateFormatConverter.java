package com.shitcode.demo1.helper;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class DateFormatConverter {
    public static Instant generateExpirationDate(Integer expDate, String format) {
        if (expDate == null || expDate <= 0 || format == null || format.isBlank()) {
            throw new IllegalArgumentException("Invalid expiration date or format.");
        }

        ChronoUnit unit;

        // Map the format string to ChronoUnit
        switch (format.toUpperCase()) {
            case "DAYS":
            case "D":
                unit = ChronoUnit.DAYS;
                break;
            case "HOURS":
            case "H":
                unit = ChronoUnit.HOURS;
                break;
            case "MINUTES":
            case "M":
                unit = ChronoUnit.MINUTES;
                break;
            case "SECONDS":
            case "S":
                unit = ChronoUnit.SECONDS;
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        // Calculate the expiration date
        return Instant.now().plus(expDate, unit);
    }

    public static Duration getDuration(Long time, String format) {
        if (time == null || time <= 0 || format == null || format.isBlank()) {
            throw new IllegalArgumentException("Invalid expiration date or format.");
        }
        Duration duration;

        // Map the format string to ChronoUnit
        switch (format.toUpperCase()) {
            case "DAYS":
            case "D":
                duration = Duration.ofDays(time);
                break;
            case "HOURS":
            case "H":
                duration = Duration.ofHours(time);
                break;
            case "MINUTES":
            case "M":
                duration = Duration.ofMinutes(time);
                break;
            case "SECONDS":
            case "S":
                duration = Duration.ofSeconds(time);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
        return duration;
    }
}
