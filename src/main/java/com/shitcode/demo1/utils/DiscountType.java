package com.shitcode.demo1.utils;

/**
 * Represents different types of discounts available in the application.
 * Each discount type has a unique identifier and a full title.
 */
public enum DiscountType {

    /** Flash sales, typically for a limited time with steep discounts. */
    FLASH_SALES("flash_sales", "Flash Sales"),

    /** Daily sales that occur every day with special deals. */
    DAILY_SALES("daily_sales", "Daily Sales"),

    /** Sales occurring on January 1st. */
    SALES_1_1("sales_1_1", "January 1st Sales"),

    /** Sales occurring on February 2nd. */
    SALES_2_2("sales_2_2", "February 2nd Sales"),

    /** Sales occurring on March 3rd. */
    SALES_3_3("sales_3_3", "March 3rd Sales"),

    /** Sales occurring on April 4th. */
    SALES_4_4("sales_4_4", "April 4th Sales"),

    /** Sales occurring on May 5th. */
    SALES_5_5("sales_5_5", "May 5th Sales"),

    /** Sales occurring on June 6th. */
    SALES_6_6("sales_6_6", "June 6th Sales"),

    /** Sales occurring on July 7th. */
    SALES_7_7("sales_7_7", "July 7th Sales"),

    /** Sales occurring on August 8th. */
    SALES_8_8("sales_8_8", "August 8th Sales"),

    /** Sales occurring on September 9th. */
    SALES_9_9("sales_9_9", "September 9th Sales"),

    /** Sales occurring on October 10th. */
    SALES_10_10("sales_10_10", "October 10th Sales"),

    /** Sales occurring on November 11th (Singles' Day, popular in some regions). */
    SALES_11_11("sales_11_11", "November 11th Sales"),

    /** Sales occurring on December 12th. */
    SALES_12_12("sales_12_12", "December 12th Sales"),

    /**
     * Seasonal sales based on different times of the year (e.g., summer, winter).
     */
    SEASONAL_SALES("seasonal_sales", "Seasonal Sales"),

    /** Clearance sales for old stock or discontinued items. */
    CLEARANCE_SALES("clearance_sales", "Clearance Sales"),

    /** Sales offered during specific holidays like Christmas or Thanksgiving. */
    HOLIDAY_SALES("holiday_sales", "Holiday Sales"),

    /** Black Friday sale, typically occurring on the Friday after Thanksgiving. */
    BLACK_FRIDAY("black_friday", "Black Friday Sale"),

    /** Cyber Monday sale, occurring on the Monday after Black Friday. */
    CYBER_MONDAY("cyber_monday", "Cyber Monday Sale"),

    /** Discounts for customers purchasing in bulk. */
    BULK_PURCHASE("bulk_purchase", "Bulk Purchase Discount"),

    /** Exclusive discounts for registered members. */
    MEMBER_EXCLUSIVE("member_exclusive", "Member Exclusive Discount"),

    /** Discount offered to first-time buyers. */
    FIRST_PURCHASE("first_purchase", "First Purchase Discount"),

    /** Discount given when a customer refers a friend. */
    REFERRAL_DISCOUNT("referral_discount", "Referral Discount");

    private final String type;
    private final String fullTitle;

    /**
     * Constructs a DiscountType with a type identifier and a full title.
     *
     * @param type      The unique identifier for the discount type.
     * @param fullTitle The full descriptive title of the discount.
     */
    DiscountType(String type, String fullTitle) {
        this.type = type;
        this.fullTitle = fullTitle;
    }

    /**
     * Gets the unique identifier of the discount type.
     *
     * @return The discount type identifier.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the full descriptive title of the discount type.
     *
     * @return The full title of the discount type.
     */
    public String getFullTitle() {
        return this.fullTitle;
    }
}
