package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment methods supported by Cheqi receipts.
 * 
 * <p>These represent the various ways a customer can pay for goods or services.</p>
 */
public enum PaymentMethod {
    /**
     * Payment by credit or debit card (physical or digital wallet).
     */
    CARD("card"),
    
    /**
     * Payment in cash.
     */
    CASH("cash"),
    
    /**
     * Payment via bank transfer or wire transfer.
     */
    BANK_TRANSFER("bank_transfer"),
    
    /**
     * Payment via digital wallet (Apple Pay, Google Pay, Samsung Pay, etc.).
     */
    DIGITAL_WALLET("digital_wallet"),
    
    /**
     * Payment via buy now, pay later services (Klarna, Afterpay, etc.).
     */
    BNPL("bnpl"),
    
    /**
     * Payment via cryptocurrency.
     */
    CRYPTO("crypto"),
    
    /**
     * Payment via check/cheque.
     */
    CHECK("check"),
    
    /**
     * Payment via direct debit.
     */
    DIRECT_DEBIT("direct_debit"),
    
    /**
     * Payment via voucher or gift card.
     */
    VOUCHER("voucher"),
    
    /**
     * Payment method not specified or other method.
     */
    OTHER("other");
    
    private final String value;
    
    PaymentMethod(String value) {
        this.value = value;
    }
    
    /**
     * Get the string value for JSON serialization.
     */
    @JsonValue
    public String getValue() {
        return value;
    }
    
    /**
     * Parse payment method from string value.
     */
    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
