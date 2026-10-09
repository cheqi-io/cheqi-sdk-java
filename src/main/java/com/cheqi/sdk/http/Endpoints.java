package com.cheqi.sdk.http;

/**
 * Defines API endpoint paths for the Cheqi SDK.
 */
public enum Endpoints {
    CUSTOMER_MATCH_ENDPOINT("/recipient/resolve"),
    MATCH_STATUS_ENDPOINT("/recipient/matches/%s"),
    ENCRYPTED_RECEIPT_ENDPOINT("/receipt/encrypted"),
    CLIENT_RECEIPT_DOWNLOAD_ENDPOINT("/receipt/download"),
    ENCRYPTED_CREDIT_NOTE_ENDPOINT("/credit-note/encrypted"),
    EMAIL_RECEIPT_ENDPOINT("/receipt/email"),
    COMPANY_INVITE_EMPLOYEES_ENDPOINT("/company/%s/invite/employees"),
    RECEIPT_DESTINATIONS_ENDPOINT("/company/receipt-destinations"),
    RECEIPT_DESTINATION_ENDPOINT("/company/receipt-destinations/%s"),
    RECEIPT_DESTINATION_QUEUE_ENDPOINT("/company/receipt-destinations/%s/receipts/queue"),
    RECEIPT_DESTINATION_ACKNOWLEDGE_ENDPOINT("/company/receipt-destinations/%s/receipts/queue/acknowledge"),
    WEBHOOK_SUBSCRIPTION_ENDPOINT("/webhook/subscription/%s"),
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
