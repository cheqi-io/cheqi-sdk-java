package com.cheqi.sdk.receipt;

public enum DeliveryStatus {
    /** Receipt successfully delivered to customer's Cheqi app/devices */
    DELIVERED_DIGITAL,
    /** Receipt sent via email (fallback) */
    DELIVERED_EMAIL,
    /** Customer not found - email required */
    CUSTOMER_NOT_FOUND,
    /** Processing failed */
    FAILED
}
