package com.cheqi.sdk.http;

import com.cheqi.commons.UBL.PurchaseReceipt;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for sending a receipt via email.
 * Contains the customer email address and the purchase receipt data.
 */
public class EmailReceiptRequest {
    @JsonProperty("customerEmail")
    private final String customerEmail;
    
    @JsonProperty("purchaseReceipt")
    private final PurchaseReceipt purchaseReceipt;

    public EmailReceiptRequest(String customerEmail, PurchaseReceipt purchaseReceipt) {
        this.customerEmail = customerEmail;
        this.purchaseReceipt = purchaseReceipt;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public PurchaseReceipt getPurchaseReceipt() {
        return purchaseReceipt;
    }
}
