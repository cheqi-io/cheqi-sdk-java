package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VatMetadata {

    @JsonProperty("supplierCountry")
    private String supplierCountry;

    @JsonProperty("buyerCountry")
    private String buyerCountry;

    @JsonProperty("crossBorder")
    private boolean crossBorder;

    @JsonProperty("recipientEntityType")
    private RecipientEntityType recipientEntityType;

    @JsonProperty("vatOnReceipt")
    private boolean vatOnReceipt;

    @JsonProperty("vatRegime")
    private VatRegime vatRegime;

    @JsonProperty("taxesApplied")
    private Boolean taxesApplied;

    public VatMetadata() {}

    public VatMetadata(String supplierCountry, String buyerCountry, boolean crossBorder,
                       RecipientEntityType recipientEntityType, boolean vatOnReceipt,
                       VatRegime vatRegime, Boolean taxesApplied) {
        this.supplierCountry = supplierCountry;
        this.buyerCountry = buyerCountry;
        this.crossBorder = crossBorder;
        this.recipientEntityType = recipientEntityType;
        this.vatOnReceipt = vatOnReceipt;
        this.vatRegime = vatRegime;
        this.taxesApplied = taxesApplied;
    }

    public String getSupplierCountry() { return supplierCountry; }
    public void setSupplierCountry(String supplierCountry) { this.supplierCountry = supplierCountry; }

    public String getBuyerCountry() { return buyerCountry; }
    public void setBuyerCountry(String buyerCountry) { this.buyerCountry = buyerCountry; }

    public boolean isCrossBorder() { return crossBorder; }
    public void setCrossBorder(boolean crossBorder) { this.crossBorder = crossBorder; }

    public RecipientEntityType getRecipientEntityType() { return recipientEntityType; }
    public void setRecipientEntityType(RecipientEntityType recipientEntityType) { this.recipientEntityType = recipientEntityType; }

    public boolean isVatOnReceipt() { return vatOnReceipt; }
    public void setVatOnReceipt(boolean vatOnReceipt) { this.vatOnReceipt = vatOnReceipt; }

    public VatRegime getVatRegime() { return vatRegime; }
    public void setVatRegime(VatRegime vatRegime) { this.vatRegime = vatRegime; }

    public Boolean getTaxesApplied() { return taxesApplied; }
    public void setTaxesApplied(Boolean taxesApplied) { this.taxesApplied = taxesApplied; }
}
