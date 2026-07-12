package com.cheqi.sdk.receipt;

public enum DeliveryStatus {
    /** Receipt successfully delivered to customer's Cheqi app/devices */
    DELIVERED_DIGITAL,
    /** Receipt sent via email (fallback) */
    DELIVERED_EMAIL,
    /** Receipt available via QR-code self-service download (fallback); see {@link ReceiptResult#getDownloadUrl()} */
    DELIVERED_DOWNLOAD,
    /** Download URL exists; canonical template generation must be retried later. */
    PENDING_DOWNLOAD_TEMPLATE,
    /** Ciphertext exists; the exact bytes must be retried later. */
    PENDING_DOWNLOAD_UPLOAD,
    /** Customer not found - email required */
    CUSTOMER_NOT_FOUND,
    /** Processing failed */
    FAILED
}
