package com.shitcode.demo1.utils;

/**
 * Enum representing different user interaction events in an e-commerce
 * application.
 * Each event corresponds to a specific user action during their shopping
 * journey.
 */
public enum InteractionEvent {

    /**
     * User selects a product category.
     */
    PICK_CATEGORY("pick_category"),

    /**
     * User searches for a product using keywords.
     */
    SEARCH_PRODUCT("search_product"),

    /**
     * User views a product's details.
     */
    VIEW_PRODUCT("view_product"),

    /**
     * User views a product and adds it to the cart.
     */
    VIEW_AND_ADD_TO_CART("view_and_add_to_cart"),

    /**
     * User views a product and proceeds to buy it immediately.
     */
    VIEW_AND_BUY("view_and_buy"),

    /**
     * User proceeds from the cart to the purchase process.
     */
    CART_TO_PURCHASE("cart_to_purchase"),

    /**
     * User chooses to pay using a credit or debit card.
     */
    PAY_BY_CARD("pay_by_card"),

    /**
     * User chooses to pay using PayPal.
     */
    PAY_BY_PAYPAL("pay_by_paypal"),

    /**
     * User chooses to pay by cash (e.g., cash on delivery).
     */
    PAY_BY_CASH("pay_by_cash");

    private final String prototype;

    /**
     * Constructor for InteractionEvent.
     *
     * @param prototype The string representation of the event.
     */
    InteractionEvent(String prototype) {
        this.prototype = prototype;
    }

    /**
     * Gets the string representation of the interaction event.
     *
     * @return The prototype string of the event.
     */
    public String getPrototype() {
        return this.prototype;
    }
}
