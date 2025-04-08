package com.shitcode.demo1.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.lang.NonNull;

public abstract class DatetimeFormat {
    /**
     * Converts a time value to a formatted string using one of three conversion
     * methods.
     *
     * @author Lvoxx
     * @param time   the time value (as Long)
     * @param mode   the conversion mode to use
     * @param format the desired datetime format enum
     * @return a formatted string representing the time
     */
    public static String format(@NonNull Long time, @NonNull TimeConversionMode mode, @NonNull Format format) {
        String formatter = format.getFormat();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter).withZone(ZoneId.systemDefault());

        switch (mode) {
            case INSTANT:
                Instant instant = Instant.ofEpochSecond(time);
                // If the format includes date components, use ZonedDateTime
                if (format.includesDate()) {
                    return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()).format(dateTimeFormatter);
                } else {
                    return LocalTime.ofInstant(instant, ZoneId.systemDefault()).format(dateTimeFormatter);
                }

            case SECOND:
            default:
                // If the format includes date components, use LocalDateTime from epoch
                if (format.includesDate()) {
                    return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC).format(dateTimeFormatter);
                } else {
                    return LocalTime.ofSecondOfDay(time).format(dateTimeFormatter);
                }
        }
    }

    public enum TimeConversionMode {
        INSTANT, // Use LocalTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())
        SECOND // Use LocalTime.ofSecondOfDay(time)
    }

    public enum Format {
        SHORT("HH:mm:ss", false),
        SHORT_PRETTY("HH'h'mm'm'ss's'", false),
        CLASSIC("dd-MM-yyyy HH:mm:ss", true),
        FORMAL("yyyy-MM-dd HH:mm:ss.SSS 'GMT'XXX", true);

        private final String format;
        private final boolean includesDate;

        Format(String format, boolean includesDate) {
            this.format = format;
            this.includesDate = includesDate;
        }

        public String getFormat() {
            return format;
        }

        public boolean includesDate() {
            return includesDate;
        }
    }
}
