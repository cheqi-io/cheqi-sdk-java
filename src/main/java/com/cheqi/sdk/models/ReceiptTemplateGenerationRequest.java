package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReceiptTemplateGenerationRequest {
    @JsonProperty("receiptTemplateRequest")
    private ReceiptTemplateRequest receiptTemplateRequest;

    @JsonProperty("formats")
    private List<ReceiptFormat> formats;

    @JsonProperty("buyerCountryCode")
    private String buyerCountryCode;

    @JsonProperty("recipientEntityType")
    private RecipientEntityType recipientEntityType;

    @JsonProperty("taxesApplied")
    private Boolean taxesApplied;

    // Constructors
    public ReceiptTemplateGenerationRequest() {}

    public ReceiptTemplateGenerationRequest(
            ReceiptTemplateRequest receipt,
            List<ReceiptFormat> formats
    ) {
        this.receiptTemplateRequest = receipt;
        this.formats = formats;
    }

    public ReceiptTemplateGenerationRequest(
            ReceiptTemplateRequest receipt,
            List<ReceiptFormat> formats,
            String buyerCountryCode,
            RecipientEntityType recipientEntityType,
            Boolean taxesApplied
    ) {
        this.receiptTemplateRequest = receipt;
        this.formats = formats;
        this.buyerCountryCode = buyerCountryCode;
        this.recipientEntityType = recipientEntityType;
        this.taxesApplied = taxesApplied;
    }

    // Getters/Setters
    public ReceiptTemplateRequest getReceipt() { return receiptTemplateRequest; }
    public void setReceipt(ReceiptTemplateRequest receipt) { this.receiptTemplateRequest = receipt; }

    public List<ReceiptFormat> getFormats() { return formats; }
    public void setFormats(List<ReceiptFormat> formats) { this.formats = formats; }

    public String getBuyerCountryCode() { return buyerCountryCode; }
    public void setBuyerCountryCode(String buyerCountryCode) { this.buyerCountryCode = buyerCountryCode; }

    public RecipientEntityType getRecipientEntityType() { return recipientEntityType; }
    public void setRecipientEntityType(RecipientEntityType recipientEntityType) { this.recipientEntityType = recipientEntityType; }

    public Boolean getTaxesApplied() { return taxesApplied; }
    public void setTaxesApplied(Boolean taxesApplied) { this.taxesApplied = taxesApplied; }
}
