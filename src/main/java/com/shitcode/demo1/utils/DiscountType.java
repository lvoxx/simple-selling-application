package com.shitcode.demo1.utils;

/**
 * Represents different types of discounts available in the application.
 * Each discount type has a unique identifier and a full title.
 * Default expired time: NOW + DEFAULT DURATION
 */
public enum DiscountType {

    /** Limited-time flash sales with steep discounts. Default duration: 2 hours. */
    FLASH_SALES("flash_sales", "Flash Sales"),

    /** Special daily deals available every day. Default duration: 12 hours. */
    DAILY_SALES("daily_sales", "Daily Sales"),

    /** Sales event occurring on January 1st. Default duration: 2 weeks. */
    SALES_1_1("sales_1_1", "January 1st Sales"),

    /** Sales event occurring on February 2nd. Default duration: 2 weeks. */
    SALES_2_2("sales_2_2", "February 2nd Sales"),

    /** Sales event occurring on March 3rd. Default duration: 2 weeks. */
    SALES_3_3("sales_3_3", "March 3rd Sales"),

    /** Sales event occurring on April 4th. Default duration: 2 weeks. */
    SALES_4_4("sales_4_4", "April 4th Sales"),

    /** Sales event occurring on May 5th. Default duration: 2 weeks. */
    SALES_5_5("sales_5_5", "May 5th Sales"),

    /** Sales event occurring on June 6th. Default duration: 2 weeks. */
    SALES_6_6("sales_6_6", "June 6th Sales"),

    /** Sales event occurring on July 7th. Default duration: 2 weeks. */
    SALES_7_7("sales_7_7", "July 7th Sales"),

    /** Sales event occurring on August 8th. Default duration: 2 weeks. */
    SALES_8_8("sales_8_8", "August 8th Sales"),

    /** Sales event occurring on September 9th. Default duration: 2 weeks. */
    SALES_9_9("sales_9_9", "September 9th Sales"),

    /** Sales event occurring on October 10th. Default duration: 2 weeks. */
    SALES_10_10("sales_10_10", "October 10th Sales"),

    /**
     * Sales event occurring on November 11th (Singles' Day in some regions).
     * Default duration: 2 weeks.
     */
    SALES_11_11("sales_11_11", "November 11th Sales"),

    /** Sales event occurring on December 12th. Default duration: 2 weeks. */
    SALES_12_12("sales_12_12", "December 12th Sales"),

    /**
     * Seasonal sales held during different times of the year (e.g., summer,
     * winter).
     * Default duration: 1 month.
     */
    SEASONAL_SALES("seasonal_sales", "Seasonal Sales"),

    /**
     * Clearance sales for discontinued or old stock items. Default duration: 2
     * weeks.
     * Default duration: 2 weeks.
     */
    CLEARANCE_SALES("clearance_sales", "Clearance Sales"),

    /**
     * Sales offered during major holidays like Christmas or Thanksgiving. Default
     * duration: 1 month.
     */
    HOLIDAY_SALES("holiday_sales", "Holiday Sales"),

    /**
     * Black Friday sale, occurring the Friday after Thanksgiving. Default duration:
     * 2 days.
     */
    BLACK_FRIDAY("black_friday", "Black Friday Sale"),

    /**
     * Cyber Monday sale, occurring the Monday following Black Friday. Default
     * duration: 1 day.
     */
    CYBER_MONDAY("cyber_monday", "Cyber Monday Sale");

    // --------------------------------------------------------------------------
    /** Unique identifier for the discount type, used for internal processing. */
    private final String type;

    /** Full descriptive title of the discount type, displayed in UI. */
    private final String fullTitle;

    /**
     * Constructs a DiscountType with a unique identifier and a descriptive title.
     *
     * @param type      The unique identifier for the discount type.
     * @param fullTitle The full descriptive title of the discount type.
     */
    DiscountType(String type, String fullTitle) {
        this.type = type;
        this.fullTitle = fullTitle;
    }

    /**
     * Retrieves the unique identifier of the discount type.
     * Used for backend processing and comparisons.
     *
     * @return The discount type identifier.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Retrieves the full descriptive title of the discount type.
     * Used for displaying user-friendly names in UI.
     *
     * @return The full title of the discount type.
     */
    public String getFullTitle() {
        return this.fullTitle;
    }
}
