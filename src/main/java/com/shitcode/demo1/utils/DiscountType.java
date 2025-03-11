package com.shitcode.demo1.utils;

public enum DiscountType {
    FLASH_SALES("flash_sales", "Flash Sales"),
    DAILY_SALES("daily_sales", "Daily Sales"),
    SALES_1_1("sales_1_1", "January 1st Sales"),
    SALES_2_2("sales_2_2", "February 2nd Sales"),
    SALES_3_3("sales_3_3", "March 3rd Sales"),
    SALES_4_4("sales_4_4", "April 4th Sales"),
    SALES_5_5("sales_5_5", "May 5th Sales"),
    SALES_6_6("sales_6_6", "June 6th Sales"),
    SALES_7_7("sales_7_7", "July 7th Sales"),
    SALES_8_8("sales_8_8", "August 8th Sales"),
    SALES_9_9("sales_9_9", "September 9th Sales"),
    SALES_10_10("sales_10_10", "October 10th Sales"),
    SALES_11_11("sales_11_11", "November 11th Sales"),
    SALES_12_12("sales_12_12", "December 12th Sales"),
    SEASONAL_SALES("seasonal_sales", "Seasonal Sales"),
    CLEARANCE_SALES("clearance_sales", "Clearance Sales"),
    HOLIDAY_SALES("holiday_sales", "Holiday Sales"),
    BLACK_FRIDAY("black_friday", "Black Friday Sale"),
    CYBER_MONDAY("cyber_monday", "Cyber Monday Sale"),
    BULK_PURCHASE("bulk_purchase", "Bulk Purchase Discount"),
    MEMBER_EXCLUSIVE("member_exclusive", "Member Exclusive Discount"),
    FIRST_PURCHASE("first_purchase", "First Purchase Discount"),
    REFERRAL_DISCOUNT("referral_discount", "Referral Discount");

    private final String type;
    private final String fullTitle;

    DiscountType(String type, String fullTitle) {
        this.type = type;
        this.fullTitle = fullTitle;
    }

    public String getType() {
        return this.type;
    }

    public String getFullTitle() {
        return this.fullTitle;
    }
}