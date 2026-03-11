package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Payment information for a receipt.
 * 
 * <p>Contains details about how the transaction was paid, including payment method,
 * card details, Payment Account Reference (PAR), cash amounts, and transaction references.</p>
 *
 * <h3>Example Usage - Card Payment:</h3>
 * <pre>
 * Payment payment = Payment.builder()
 *     .paymentMethod(PaymentMethod.CARD)
 *     .par("PAR1234567890ABCDEF")  // Payment Account Reference
 *     .cardIssuer("Visa")
 *     .cardBrand("Visa Debit")
 *     .lastFourDigits("4242")
 *     .transactionReference("TXN-2024-001")
 *     .authorizationCode("AUTH123456")
 *     .build();
 * </pre>
 *
 * <h3>Example Usage - Cash Payment:</h3>
 * <pre>
 * Payment payment = Payment.builder()
 *     .paymentMethod(PaymentMethod.CASH)
 *     .amountTendered(new BigDecimal("50.00"))
 *     .changeGiven(new BigDecimal("3.55"))
 *     .transactionReference("CASH-2024-001")
 *     .build();
 * </pre>
 *
 * <h3>Example Usage - Bank Transfer:</h3>
 * <pre>
 * Payment payment = Payment.builder()
 *     .paymentMethod(PaymentMethod.BANK_TRANSFER)
 *     .transactionReference("IBAN-REF-123")
 *     .authorizationCode("BANK-AUTH-456")
 *     .build();
 * </pre>
 *
 * <h3>Payment Account Reference (PAR):</h3>
 * <p>The PAR is a non-financial identifier that uniquely represents a payment card account.
 * Unlike the PAN (Primary Account Number), the PAR:</p>
 * <ul>
 *   <li>Remains consistent across card replacements (e.g., lost, stolen, expired cards)</li>
 *   <li>Is non-sensitive and can be stored without PCI compliance concerns</li>
 *   <li>Enables merchants to identify returning customers without storing card numbers</li>
 *   <li>Facilitates receipt matching and customer recognition</li>
 * </ul>
 *
 * @see PaymentMethod
 * @see CheqiReceipt
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Payment.Builder.class)
public final class Payment {
    
    /**
     * The payment method used for this transaction.
     * MANDATORY - must be specified.
     */
    @JsonProperty("paymentMethod")
    private final PaymentMethod paymentMethod;
    
    // ===== CARD PAYMENT FIELDS =====
    
    /**
     * Payment Account Reference (PAR) - a unique, non-financial identifier for a payment card account.
     * Remains constant even when the card is replaced.
     * Recommended for card payments to enable customer recognition.
     */
    @JsonProperty("par")
    private final String par;
    
    /**
     * Card issuer/network (e.g., "Visa", "Mastercard", "American Express", "Maestro").
     * For card payments.
     */
    @JsonProperty("cardIssuer")
    private final String cardIssuer;
    
    /**
     * Specific card brand/product (e.g., "Visa Debit", "Mastercard Credit", "Visa Business").
     * More specific than cardIssuer.
     * For card payments.
     */
    @JsonProperty("cardBrand")
    private final String cardBrand;
    
    /**
     * Last 4 digits of the card number for display purposes.
     * For card payments.
     */
    @JsonProperty("lastFourDigits")
    private final String lastFourDigits;
    
    /**
     * Authorization code from the payment processor.
     * For card and some digital payments.
     */
    @JsonProperty("authorizationCode")
    private final String authorizationCode;
    
    // ===== CASH PAYMENT FIELDS =====
    
    /**
     * Amount given by the customer in cash.
     * For cash payments where change is given.
     */
    @JsonProperty("amountTendered")
    private final BigDecimal amountTendered;
    
    /**
     * Change returned to the customer.
     * For cash payments.
     */
    @JsonProperty("changeGiven")
    private final BigDecimal changeGiven;
    
    // ===== GENERAL PAYMENT FIELDS =====
    
    /**
     * Unique transaction reference/identifier from the payment processor or POS system.
     * Can be used for reconciliation and refunds.
     */
    @JsonProperty("transactionReference")
    private final String transactionReference;
    
    /**
     * Payment processor or gateway name (e.g., "Stripe", "Adyen", "Square").
     */
    @JsonProperty("processor")
    private final String processor;
    
    /**
     * Terminal ID where the payment was processed.
     * Useful for physical stores with multiple terminals.
     */
    @JsonProperty("terminalId")
    private final String terminalId;
    
    /**
     * Additional payment notes or metadata.
     */
    @JsonProperty("notes")
    private final String notes;
    
    private final Map<String, Object> additionalProperties;
    
    // ===== CONSTRUCTOR =====
    
    private Payment(
            PaymentMethod paymentMethod,
            String par,
            String cardIssuer,
            String cardBrand,
            String lastFourDigits,
            String authorizationCode,
            BigDecimal amountTendered,
            BigDecimal changeGiven,
            String transactionReference,
            String processor,
            String terminalId,
            String notes,
            Map<String, Object> additionalProperties) {
        this.paymentMethod = paymentMethod;
        this.par = par;
        this.cardIssuer = cardIssuer;
        this.cardBrand = cardBrand;
        this.lastFourDigits = lastFourDigits;
        this.authorizationCode = authorizationCode;
        this.amountTendered = amountTendered;
        this.changeGiven = changeGiven;
        this.transactionReference = transactionReference;
        this.processor = processor;
        this.terminalId = terminalId;
        this.notes = notes;
        this.additionalProperties = additionalProperties;
    }
    
    // ===== ACCESSORS =====
    
    @JsonIgnore
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getPar() {
        return par;
    }
    
    public String getCardIssuer() {
        return cardIssuer;
    }
    
    public String getCardBrand() {
        return cardBrand;
    }
    
    public String getLastFourDigits() {
        return lastFourDigits;
    }
    
    public String getAuthorizationCode() {
        return authorizationCode;
    }
    
    public BigDecimal getAmountTendered() {
        return amountTendered;
    }
    
    public BigDecimal getChangeGiven() {
        return changeGiven;
    }
    
    public String getTransactionReference() {
        return transactionReference;
    }
    
    public String getProcessor() {
        return processor;
    }
    
    public String getTerminalId() {
        return terminalId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Payment && equalTo((Payment) other);
    }
    
    private boolean equalTo(Payment other) {
        return Objects.equals(this.paymentMethod, other.paymentMethod)
                && Objects.equals(this.par, other.par)
                && Objects.equals(this.cardIssuer, other.cardIssuer)
                && Objects.equals(this.cardBrand, other.cardBrand)
                && Objects.equals(this.lastFourDigits, other.lastFourDigits)
                && Objects.equals(this.authorizationCode, other.authorizationCode)
                && Objects.equals(this.amountTendered, other.amountTendered)
                && Objects.equals(this.changeGiven, other.changeGiven)
                && Objects.equals(this.transactionReference, other.transactionReference)
                && Objects.equals(this.processor, other.processor)
                && Objects.equals(this.terminalId, other.terminalId)
                && Objects.equals(this.notes, other.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.paymentMethod, this.par, this.cardIssuer, this.cardBrand,
                this.lastFourDigits, this.authorizationCode, this.amountTendered, this.changeGiven,
                this.transactionReference, this.processor, this.terminalId, this.notes);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentMethod=" + paymentMethod +
                ", par=" + par +
                ", cardIssuer=" + cardIssuer +
                ", cardBrand=" + cardBrand +
                ", lastFourDigits=" + lastFourDigits +
                ", authorizationCode=" + authorizationCode +
                ", amountTendered=" + amountTendered +
                ", changeGiven=" + changeGiven +
                ", transactionReference=" + transactionReference +
                ", processor=" + processor +
                ", terminalId=" + terminalId +
                ", notes=" + notes +
                '}';
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private PaymentMethod paymentMethod;
        private String par;
        private String cardIssuer;
        private String cardBrand;
        private String lastFourDigits;
        private String authorizationCode;
        private BigDecimal amountTendered;
        private BigDecimal changeGiven;
        private String transactionReference;
        private String processor;
        private String terminalId;
        private String notes;
        private Map<String, Object> additionalProperties = new HashMap<>();
        
        private Builder() {}
        
        @JsonSetter(value = "paymentMethod", nulls = Nulls.SKIP)
        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }
        
        @JsonSetter(value = "par", nulls = Nulls.SKIP)
        public Builder par(String par) {
            this.par = par;
            return this;
        }
        
        @JsonSetter(value = "cardIssuer", nulls = Nulls.SKIP)
        public Builder cardIssuer(String cardIssuer) {
            this.cardIssuer = cardIssuer;
            return this;
        }
        
        @JsonSetter(value = "cardBrand", nulls = Nulls.SKIP)
        public Builder cardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
            return this;
        }
        
        @JsonSetter(value = "lastFourDigits", nulls = Nulls.SKIP)
        public Builder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }
        
        @JsonSetter(value = "authorizationCode", nulls = Nulls.SKIP)
        public Builder authorizationCode(String authorizationCode) {
            this.authorizationCode = authorizationCode;
            return this;
        }
        
        @JsonSetter(value = "amountTendered", nulls = Nulls.SKIP)
        public Builder amountTendered(BigDecimal amountTendered) {
            this.amountTendered = amountTendered;
            return this;
        }
        
        public Builder amountTendered(String amountTendered) {
            this.amountTendered = amountTendered != null ? new BigDecimal(amountTendered) : null;
            return this;
        }
        
        @JsonSetter(value = "changeGiven", nulls = Nulls.SKIP)
        public Builder changeGiven(BigDecimal changeGiven) {
            this.changeGiven = changeGiven;
            return this;
        }
        
        public Builder changeGiven(String changeGiven) {
            this.changeGiven = changeGiven != null ? new BigDecimal(changeGiven) : null;
            return this;
        }
        
        @JsonSetter(value = "transactionReference", nulls = Nulls.SKIP)
        public Builder transactionReference(String transactionReference) {
            this.transactionReference = transactionReference;
            return this;
        }
        
        @JsonSetter(value = "processor", nulls = Nulls.SKIP)
        public Builder processor(String processor) {
            this.processor = processor;
            return this;
        }
        
        @JsonSetter(value = "terminalId", nulls = Nulls.SKIP)
        public Builder terminalId(String terminalId) {
            this.terminalId = terminalId;
            return this;
        }
        
        @JsonSetter(value = "notes", nulls = Nulls.SKIP)
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }
        
        public Payment build() {
            return new Payment(
                    paymentMethod, par, cardIssuer, cardBrand, lastFourDigits,
                    authorizationCode, amountTendered, changeGiven, transactionReference,
                    processor, terminalId, notes, additionalProperties);
        }
    }
}
