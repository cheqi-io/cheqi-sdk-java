package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CheqiReceipt {
    @JsonProperty("documentNumber")
    private String documentNumber;
    
    @JsonProperty("identifiers")
    private List<Identifier> identifiers;
    
    @JsonProperty("issueDate")
    private Instant issueDate;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("receiptSubtotal")
    private BigDecimal receiptSubtotal;
    
    @JsonProperty("totalBeforeTax")
    private BigDecimal totalBeforeTax;
    
    @JsonProperty("totalTaxAmount")
    private BigDecimal totalTaxAmount;
    
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    
    @JsonProperty("products")
    private List<Product> products;
    
    @JsonProperty("discounts")
    private List<Discount> discounts;
    
    @JsonProperty("charges")
    private List<Charge> charges;
    
    @JsonProperty("taxes")
    private List<Tax> taxes;
    
    @JsonProperty("period")
    private Period period;
    
    @JsonProperty("note")
    private String note;
    
    @JsonProperty("barcodes")
    private List<Barcode> barcodes;

    @JsonProperty("sellingParty")
    private SellingParty sellingParty;
    
    @JsonProperty("receivingParty")
    private ReceivingParty receivingParty;
    
    @JsonProperty("verificationNonce")
    private String verificationNonce;
    
    @JsonProperty("paymentInfo")
    private PaymentInfo paymentInfo;

    public CheqiReceipt() {
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Instant getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getReceiptSubtotal() {
        return receiptSubtotal;
    }

    public void setReceiptSubtotal(BigDecimal receiptSubtotal) {
        this.receiptSubtotal = receiptSubtotal;
    }

    public BigDecimal getTotalBeforeTax() {
        return totalBeforeTax;
    }

    public void setTotalBeforeTax(BigDecimal totalBeforeTax) {
        this.totalBeforeTax = totalBeforeTax;
    }

    public BigDecimal getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public void setCharges(List<Charge> charges) {
        this.charges = charges;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Barcode> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<Barcode> barcodes) {
        this.barcodes = barcodes;
    }

    public SellingParty getSellingParty() {
        return sellingParty;
    }

    public void setSellingParty(SellingParty sellingParty) {
        this.sellingParty = sellingParty;
    }

    public ReceivingParty getReceivingParty() {
        return receivingParty;
    }

    public void setReceivingParty(ReceivingParty receivingParty) {
        this.receivingParty = receivingParty;
    }

    public String getVerificationNonce() {
        return verificationNonce;
    }

    public void setVerificationNonce(String verificationNonce) {
        this.verificationNonce = verificationNonce;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
}
