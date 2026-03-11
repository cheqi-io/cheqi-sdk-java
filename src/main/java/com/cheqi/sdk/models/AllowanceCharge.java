package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Allowance charge DTO representing discounts or additional charges.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>chargeIndicator</strong>: True for charge, false for allowance (discount)</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>allowanceChargeReasonCode</strong>: Reason code for the allowance/charge</li>
 *   <li><strong>allowanceChargeReason</strong>: Text description of the reason</li>
 *   <li><strong>percentage</strong>: Percentage for calculation</li>
 *   <li><strong>amount</strong>: Fixed amount (without VAT)</li>
 * </ul>
 *
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>Either amount OR percentage should be provided for calculation</li>
 *   <li>If percentage is provided, it's used with base amount: Base Amount * (Percentage / 100)</li>
 * </ul>
 *
 * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/2023-Q2/codelist/UNCL5189/">Allowance Reason Codes</a>
 * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL7161/">Charge Reason Codes</a>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = AllowanceCharge.Builder.class)
public final class AllowanceCharge {

    // ===== MANDATORY FIELDS =====

    /**
     * Indicates if this is a charge (true) or an allowance/discount (false).
     */
    @JsonProperty("chargeIndicator")
    private final Boolean chargeIndicator;

    // ===== OPTIONAL FIELDS =====

    /**
     * The reason for the allowance or charge, expressed as a code.
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/2023-Q2/codelist/UNCL5189/">Allowance Reason Codes</a>
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL7161/">Charge Reason Codes</a>
     */
    @JsonProperty("allowanceChargeReasonCode")
    private final String allowanceChargeReasonCode;

    /**
     * The reason for the allowance or charge, expressed as text.
     * Examples: "Summer sale", "Early Payment Discounts", "Extra processing fee".
     * If not provided, the description matching the allowanceChargeReasonCode will be used.
     */
    @JsonProperty("allowanceChargeReason")
    private final String allowanceChargeReason;

    /**
     * The percentage that may be used, in conjunction with the base amount, 
     * to calculate the allowance or charge amount.
     */
    @JsonProperty("percentage")
    private final Double percentage;

    /**
     * The amount of an allowance or a charge, without VAT.
     * This is typically calculated as: Base Amount * (Percentage / 100).
     */
    @JsonProperty("allowanceAmount")
    private final BigDecimal amount;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private AllowanceCharge(
            Boolean chargeIndicator,
            String allowanceChargeReasonCode,
            String allowanceChargeReason,
            Double percentage,
            BigDecimal amount,
            Map<String, Object> additionalProperties) {
        this.chargeIndicator = chargeIndicator;
        this.allowanceChargeReasonCode = allowanceChargeReasonCode;
        this.allowanceChargeReason = allowanceChargeReason;
        this.percentage = percentage;
        this.amount = amount;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    /**
     * @return Indicates if this is a charge (true) or an allowance/discount (false)
     */
    @JsonIgnore
    public Boolean getChargeIndicator() {
        return chargeIndicator;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The allowance charge reason code if provided
     */
    public String getAllowanceChargeReasonCode() {
        return allowanceChargeReasonCode;
    }

    /**
     * @return The allowance charge reason text if provided
     */
    public String getAllowanceChargeReason() {
        return allowanceChargeReason;
    }

    /**
     * @return The percentage if provided
     */
    public Double getPercentage() {
        return percentage;
    }

    /**
     * @return The amount if provided
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return Additional properties map
     */
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof AllowanceCharge && equalTo((AllowanceCharge) other);
    }

    private boolean equalTo(AllowanceCharge other) {
        return Objects.equals(this.chargeIndicator, other.chargeIndicator)
                && Objects.equals(this.allowanceChargeReasonCode, other.allowanceChargeReasonCode)
                && Objects.equals(this.allowanceChargeReason, other.allowanceChargeReason)
                && Objects.equals(this.percentage, other.percentage)
                && Objects.equals(this.amount, other.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.chargeIndicator,
                this.allowanceChargeReasonCode,
                this.allowanceChargeReason,
                this.percentage,
                this.amount
        );
    }

    @Override
    public String toString() {
        return "AllowanceCharge{" +
                "chargeIndicator=" + chargeIndicator +
                ", allowanceChargeReasonCode=" + allowanceChargeReasonCode +
                ", allowanceChargeReason=" + allowanceChargeReason +
                ", percentage=" + percentage +
                ", amount=" + amount +
                '}';
    }

    public static AllowanceCharge.Builder builder() {
        return new AllowanceCharge.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Boolean chargeIndicator;
        private String allowanceChargeReasonCode;
        private String allowanceChargeReason;
        private Double percentage;
        private BigDecimal amount;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public AllowanceCharge.Builder from(AllowanceCharge other) {
            chargeIndicator(other.getChargeIndicator());
            allowanceChargeReasonCode(other.getAllowanceChargeReasonCode());
            allowanceChargeReason(other.getAllowanceChargeReason());
            percentage(other.getPercentage());
            amount(other.getAmount());

            return this;
        }

        @JsonSetter(value = "chargeIndicator", nulls = Nulls.SKIP)
        public AllowanceCharge.Builder chargeIndicator(Boolean chargeIndicator) {
            this.chargeIndicator = chargeIndicator;
            return this;
        }

        @JsonSetter(value = "allowanceChargeReasonCode", nulls = Nulls.SKIP)
        public AllowanceCharge.Builder allowanceChargeReasonCode(String allowanceChargeReasonCode) {
            this.allowanceChargeReasonCode = allowanceChargeReasonCode;
            return this;
        }

        @JsonSetter(value = "allowanceChargeReason", nulls = Nulls.SKIP)
        public AllowanceCharge.Builder allowanceChargeReason(String allowanceChargeReason) {
            this.allowanceChargeReason = allowanceChargeReason;
            return this;
        }

        @JsonSetter(value = "percentage", nulls = Nulls.SKIP)
        public AllowanceCharge.Builder percentage(Double percentage) {
            this.percentage = percentage;
            return this;
        }

        @JsonSetter(value = "allowanceAmount", nulls = Nulls.SKIP)
        public AllowanceCharge.Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public AllowanceCharge build() {
            return new AllowanceCharge(
                    chargeIndicator,
                    allowanceChargeReasonCode,
                    allowanceChargeReason,
                    percentage,
                    amount,
                    additionalProperties
            );
        }
    }

}