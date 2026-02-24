package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReceiptTemplateGenerationRequest {
    @JsonProperty("receiptTemplateRequest")
    private ReceiptTemplateRequest receiptTemplateRequest;

    @JsonProperty("formats")
    private List<ReceiptFormat> formats;

    // Constructors
    public ReceiptTemplateGenerationRequest() {}

    public ReceiptTemplateGenerationRequest(
            ReceiptTemplateRequest receipt,
            List<ReceiptFormat> formats
    ) {
        this.receiptTemplateRequest = receipt;
        this.formats = formats;
    }

    // Getters/Setters
    public ReceiptTemplateRequest getReceipt() { return receiptTemplateRequest; }
    public void setReceipt(ReceiptTemplateRequest receipt) { this.receiptTemplateRequest = receipt; }

    public List<ReceiptFormat> getFormats() { return formats; }
    public void setFormats(List<ReceiptFormat> formats) { this.formats = formats; }
}
