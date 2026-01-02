package com.cheqi.sdk.http;

/**
 * Defines API endpoint paths for the Cheqi SDK.
 */
public enum Endpoints {
    TEMPLATE_ENDPOINT("/receipt/template"),
    CUSTOMER_MATCH_ENDPOINT("/recipient/resolve"),
    ENCRYPTED_RECEIPT_ENDPOINT("/receipt/encrypted"),
    ENCRYPTED_CREDIT_NOTE_ENDPOINT("/credit-note/encrypted"),
    CREDIT_NOTE_TEMPLATE_ENDPOINT("/credit-note/template"),
    EMAIL_RECEIPT_ENDPOINT("/receipt/email"),
    COMPANY_PROVISION_ENDPOINT("/company/provision"),
    COMPANY_STORES_ENDPOINT("/company/%s/stores"),
    COMPANY_STORE_ENDPOINT("/company/%s/stores/%s"),
    COMPANY_STORE_ACTIVATE_ENDPOINT("/company/%s/stores/%s/activate"),
    COMPANY_STORE_DEACTIVATE_ENDPOINT("/company/%s/stores/%s/deactivate");

    private final String path;

    Endpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getPath(Object... args) {
        return String.format(path, args);
    }
}
