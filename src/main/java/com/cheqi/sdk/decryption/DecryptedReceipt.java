package com.cheqi.sdk.decryption;

public class DecryptedReceipt {
    private final String receiptContentJson;
    private final String customerDetails;

    public DecryptedReceipt(String receiptContentJson, String customerDetails) {
        this.receiptContentJson = receiptContentJson;
        this.customerDetails = customerDetails;
    }

    public String getReceiptContentJson() {
        return receiptContentJson;
    }

    public String getCustomerDetails() {
        return customerDetails;
    }

    public boolean hasCustomerDetails() {
        return customerDetails != null && !customerDetails.trim().isEmpty();
    }
}
