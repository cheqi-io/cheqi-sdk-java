package com.cheqi.sdk.receipt;

/** Checkout outcome, distinct from the backend's device-processing status. */
public enum ReceiptIssueState {
    DIGITAL_SUBMITTED,
    DIGITAL_PENDING,
    /** The match expired before a digital submission was accepted. */
    DIGITAL_EXPIRED,
    DOWNLOAD_FALLBACK,
    /** Existing explicit email route; the integration must complete email delivery. */
    EMAIL_FALLBACK
}
