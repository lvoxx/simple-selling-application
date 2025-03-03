package com.shitcode.demo1.utils;

public enum InteractionEvent {
    PICK_CATEGORY("pick_category"),
    SEARCH_PRODUCT("search_product"),
    VIEW_PRODUCT("view_product"),
    VIEW_AND_ADD_TO_CART("view_and_add_to_cart"),
    VIEW_AND_BUY("view_and_buy"),
    CART_TO_PURCHASE("cart_to_purchase"),
    PAY_BY_CARD("pay_by_card"),
    PAY_BY_PAYPAL("pay_by_paypal"),
    PAY_BY_CASH("pay_by_cash");

    private final String prototype;

    InteractionEvent(String prototype) {
        this.prototype = prototype;
    }

    public String getPrototype() {
        return this.prototype;
    }

}