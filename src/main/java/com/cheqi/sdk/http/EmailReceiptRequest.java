package com.cheqi.sdk.http;

import com.cheqi.sdk.models.CheqiReceipt;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for sending a receipt via email.
 * Contains the customer email address and the purchase receipt data.
 */
public class EmailReceiptRequest {
    @JsonProperty("customerEmail")
    private final String customerEmail;
    
    @JsonProperty("purchaseReceipt")
    private final CheqiReceipt cheqiReceipt;

    public EmailReceiptRequest(String customerEmail, CheqiReceipt cheqiReceipt) {
        this.customerEmail = customerEmail;
        this.cheqiReceipt = cheqiReceipt;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public CheqiReceipt getPurchaseReceipt() {
        return cheqiReceipt;
    }
}
