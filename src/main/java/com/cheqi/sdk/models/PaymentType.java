package com.cheqi.sdk.models;

public enum PaymentType {
    CARD_PAYMENT("48"),
    DIRECT_DEBIT("49"),
    CASH("10");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

