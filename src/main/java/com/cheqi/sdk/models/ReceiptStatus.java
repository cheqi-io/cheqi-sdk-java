package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Receipt lifecycle status.
 * 
 * <p>Tracks the current state of a receipt from creation through delivery to the customer.</p>
 */
public enum ReceiptStatus {
    /**
     * Receipt has been created but not yet processed for delivery.
     */
    PENDING("pending"),
    
    /**
     * Receipt is being processed (matching customer, preparing for delivery).
     */
    PROCESSING("processing"),
    
    /**
     * Receipt has been sent to the customer (via app, email, webhook, etc.).
     */
    SENT("sent"),
    
    /**
     * Receipt was successfully delivered and confirmed received.
     */
    DELIVERED("delivered"),
    
    /**
     * Delivery failed (customer not found, email bounced, etc.).
     */
    FAILED("failed"),
    
    /**
     * Receipt has been cancelled or voided.
     */
    CANCELLED("cancelled"),
    
    /**
     * Receipt has been refunded or credited back to customer.
     */
    REFUNDED("refunded");
    
    private final String value;
    
    ReceiptStatus(String value) {
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
     * Parse receipt status from string value.
     */
    @JsonCreator
    public static ReceiptStatus fromValue(String value) {
        for (ReceiptStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown receipt status: " + value);
    }
    
    /**
     * Check if this status represents a final state (no further processing expected).
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }
    
    /**
     * Check if this status represents a failure state.
     */
    public boolean isFailure() {
        return this == FAILED;
    }
    
    /**
     * Check if this status represents a successful delivery.
     */
    public boolean isSuccess() {
        return this == SENT || this == DELIVERED;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
