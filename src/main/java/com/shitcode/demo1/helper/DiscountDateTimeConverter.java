package com.shitcode.demo1.helper;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;

import com.shitcode.demo1.exception.model.DiscountOverTimeException;
import com.shitcode.demo1.utils.DiscountType;

public abstract class DiscountDateTimeConverter {
    private static final EnumSet<DiscountType> SEVEN_DAYS_DISCOUNTS = EnumSet.range(DiscountType.SALES_1_1,
            DiscountType.SALES_12_12);
    private static final EnumSet<DiscountType> THREE_DAYS_DISCOUNTS = EnumSet.of(
            DiscountType.SEASONAL_SALES, DiscountType.CLEARANCE_SALES, DiscountType.HOLIDAY_SALES);

    /**
     * Converts a {@link DiscountType} to its corresponding expiration
     * {@link OffsetDateTime} based on the current time.
     *
     * @param discountType The type of discount to convert.
     * @return The calculated expiration time for the given discount type.
     * @throws UnsupportedOperationException If the discount type requires manual
     *                                       handling.
     */
    public static OffsetDateTime convert(DiscountType discountType) {
        return convert(discountType, OffsetDateTime.now());
    }

    /**
     * Converts a {@link DiscountType} to its corresponding expiration
     * {@link OffsetDateTime} based on a given reference time.
     *
     * @param discountType The type of discount to convert.
     * @param time         The reference time for calculating the expiration.
     * @return The calculated expiration time for the given discount type.
     * @throws UnsupportedOperationException If the discount type requires manual
     *                                       handling.
     */
    public static OffsetDateTime convert(DiscountType discountType, OffsetDateTime time) {
        switch (discountType) {
            case FLASH_SALES:
                return time.plusHours(2);
            case DAILY_SALES:
                return time.plusHours(12);
            case BLACK_FRIDAY:
                return handleBlackFriday(time);
            case CYBER_MONDAY:
                return handleCyberMonday(time);
            default:
                if (SEVEN_DAYS_DISCOUNTS.contains(discountType)) {
                    return handleSalesX_X(time, discountType);
                } else if (THREE_DAYS_DISCOUNTS.contains(discountType)) {
                    return time.plusDays(3);
                } else {
                    throw new UnsupportedOperationException(
                            "This discount type requires manual handling: " + discountType);
                }
        }
    }

    private static OffsetDateTime handleSalesX_X(OffsetDateTime now, DiscountType discountType) {
        int month = Integer.parseInt(discountType.getType().split("_")[1]);
        OffsetDateTime salesDate = now.withMonth(month).withDayOfMonth(1).plusDays(6); // 7 days from the 1st
        if (now.isAfter(salesDate)) {
            throw new DiscountOverTimeException("Discount time exceeded for " + discountType.getFullTitle());
        }
        return salesDate;
    }

    private static OffsetDateTime handleBlackFriday(OffsetDateTime now) {
        OffsetDateTime blackFriday = now.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
        if (now.isAfter(blackFriday)) {
            throw new DiscountOverTimeException("Black Friday discount has expired.");
        }
        return blackFriday.plusDays(2);
    }

    private static OffsetDateTime handleCyberMonday(OffsetDateTime now) {
        if (now.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new DiscountOverTimeException("Cyber Monday discount can only be applied on a Monday.");
        }
        return now.plusDays(1);
    }
}
