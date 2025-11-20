package com.cheqi.sdk.http;

/**
 * Defines API endpoint paths for the Cheqi SDK.
 */
public enum Endpoints {
    TEMPLATE_ENDPOINT("/receipt/template"),
    CUSTOMER_MATCH_ENDPOINT("/recipient/resolve"),
    ENCRYPTED_RECEIPT_ENDPOINT("/receipt/encrypted"),
    EMAIL_RECEIPT_ENDPOINT("/receipt/email");

    private final String path;

    Endpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
