package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Represents a Cheqi receipt as returned by the backend after template generation.
 * This is the JSON format used by mobile apps to display receipt data.
 *
 * <p>This class is primarily used for deserialization — the SDK receives it from the
 * backend after calling {@code generateReceiptTemplate()}. Merchants typically don't
 * construct this directly; instead they build a {@link ReceiptTemplateRequest} and
 * the backend generates the CheqiReceipt from it.</p>
 *
 * @see ReceiptTemplateRequest
 */
public class CheqiReceipt {

    /** Merchant-assigned document number (e.g. "INV-2026-001"). */
    @JsonProperty("documentNumber")
    private String documentNumber;

    /** Additional identifiers such as order references or transaction numbers. */
    @JsonProperty("identifiers")
    private List<Identifier> identifiers;

    /** When the receipt was issued (ISO 8601). */
    @JsonProperty("issueDate")
    private Instant issueDate;

    /** ISO 4217 currency code (e.g. "EUR", "USD"). */
    @JsonProperty("currency")
    private String currency;

    /** Receipt subtotal before discounts, charges, and taxes. */
    @JsonProperty("receiptSubtotal")
    private BigDecimal receiptSubtotal;

    /** Total after discounts and charges, before tax. */
    @JsonProperty("totalBeforeTax")
    private BigDecimal totalBeforeTax;

    /** Total tax amount across all tax rates. */
    @JsonProperty("totalTaxAmount")
    private BigDecimal totalTaxAmount;

    /** Final total amount including tax. */
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    /** Line items / products on the receipt. */
    @JsonProperty("products")
    private List<Product> products;

    /** Receipt-level discounts (not per-product). */
    @JsonProperty("discounts")
    private List<Discount> discounts;

    /** Receipt-level extra charges (not per-product). */
    @JsonProperty("charges")
    private List<Charge> charges;

    /** Tax breakdown by rate. */
    @JsonProperty("taxes")
    private List<Tax> taxes;

    /** Optional billing/service period for time-based receipts. */
    @JsonProperty("period")
    private Period period;

    /** Optional free-text note from the merchant. */
    @JsonProperty("note")
    private String note;

    /**
     * Receipt-level barcodes (QR codes, EAN, CODE_128, etc.).
     * Used for return codes, loyalty programs, ticket codes, etc.
     * Per-product barcodes are stored on each {@link Product}.
     */
    @JsonProperty("barcodes")
    private List<Barcode> barcodes;

    /** Merchant/seller information (name, address, tax ID, etc.). */
    @JsonProperty("sellingParty")
    private SellingParty sellingParty;

    /** Optional customer/receiver information. */
    @JsonProperty("receivingParty")
    private ReceivingParty receivingParty;

    /** Nonce used for receipt integrity verification. */
    @JsonProperty("verificationNonce")
    private String verificationNonce;

    /** Payment method and transaction details. */
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
