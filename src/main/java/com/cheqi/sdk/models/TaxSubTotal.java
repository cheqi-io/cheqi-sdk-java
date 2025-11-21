package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Tax subtotal DTO representing detailed tax breakdown for a specific tax category/rate.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>taxableAmount</strong>: Total sum of invoice line items subject to this tax rate</li>
 *   <li><strong>taxAmount</strong>: Total tax amount calculated for this tax rate</li>
 *   <li><strong>taxCategory</strong>: Tax category ID (UNCL5305)</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>percent</strong>: Tax rate percentage</li>
 *   <li><strong>taxScheme</strong>: Tax scheme (VAT, GST, etc.)</li>
 *   <li><strong>taxExemptionReasonCode</strong>: Code for tax exemption reason</li>
 *   <li><strong>taxExemptionReason</strong>: Text description of tax exemption</li>
 * </ul>
 *
 * <h3>Tax Category Examples (UNCL5305):</h3>
 * <ul>
 *   <li><strong>S</strong>: Standard rate</li>
 *   <li><strong>Z</strong>: Zero rated</li>
 *   <li><strong>E</strong>: Exempt from tax</li>
 *   <li><strong>AE</strong>: VAT Reverse Charge</li>
 *   <li><strong>K</strong>: VAT exempt for EEA intra-community supply</li>
 * </ul>
 *
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>taxAmount = taxableAmount × (percent / 100)</li>
 *   <li>Tax exemption fields are only relevant when tax category indicates exemption</li>
 * </ul>
 *
 * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL5305/">UNCL5305 Tax Category Codes</a>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = TaxSubTotal.Builder.class)
public final class TaxSubTotal {

    // ===== MANDATORY FIELDS =====
    /**
     * The total sum of invoice line items that are subject to taxation under this specific tax rate.
     * This is calculated by summing up the individual taxable amounts of all invoice line items 
     * that fall under this tax rate.
     */
    @JsonProperty("taxableAmount")
    private final BigDecimal taxableAmount;

    /**
     * The total tax amount calculated based on the taxable amount and the tax rate.
     * Formula: taxAmount = taxableAmount × (percent / 100)
     */
    @JsonProperty("taxAmount")
    private final BigDecimal taxAmount;

    /**
     * The tax category ID according to UNCL5305.
     * Examples: "S" (standard), "Z" (zero rated), "E" (exempt)
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL5305/">UNCL5305 Tax Category Codes</a>
     */
    @JsonProperty("taxCategoryId")
    private final String taxCategoryId;

    // ===== OPTIONAL FIELDS =====
    /**
     * The tax rate in percent.
     * This is the percentage of the taxable amount that is added as tax.
     * Example: 21.0 for 21% VAT rate
     */
    @JsonProperty("percent")
    private final Optional<Double> percent;

    /**
     * The tax scheme identifier.
     * Examples: "VAT", "GST", "AAA" (petroleum tax)
     */
    @JsonProperty("taxScheme")
    private final Optional<String> taxScheme;

    /**
     * The code for the tax exemption reason according to UNCL5305.
     * Only relevant when the tax category indicates an exemption.
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL5305/">UNCL5305 Tax Category Codes</a>
     */
    @JsonProperty("taxExemptionReasonCode")
    private final Optional<String> taxExemptionReasonCode;

    /**
     * The text description of the tax exemption reason.
     * Only relevant when the tax category indicates an exemption.
     */
    @JsonProperty("taxExemptionReason")
    private final Optional<String> taxExemptionReason;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private TaxSubTotal(
            BigDecimal taxableAmount,
            BigDecimal taxAmount,
            String taxCategoryId,
            Optional<Double> percent,
            Optional<String> taxScheme,
            Optional<String> taxExemptionReasonCode,
            Optional<String> taxExemptionReason,
            Map<String, Object> additionalProperties) {
        this.taxableAmount = taxableAmount;
        this.taxAmount = taxAmount;
        this.taxCategoryId = taxCategoryId;
        this.percent = percent;
        this.taxScheme = taxScheme;
        this.taxExemptionReasonCode = taxExemptionReasonCode;
        this.taxExemptionReason = taxExemptionReason;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    @JsonIgnore
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    @JsonIgnore
    public String getTaxCategoryId() {
        return taxCategoryId;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The tax percentage if provided
     */
    @JsonIgnore
    public Optional<Double> getPercent() {
        return percent != null ? percent : Optional.empty();
    }

    /**
     * @return The tax scheme if provided
     */
    @JsonIgnore
    public Optional<String> getTaxScheme() {
        return taxScheme != null ? taxScheme : Optional.empty();
    }

    /**
     * @return The tax exemption reason code if provided
     */
    @JsonIgnore
    public Optional<String> getTaxExemptionReasonCode() {
        return taxExemptionReasonCode != null ? taxExemptionReasonCode : Optional.empty();
    }

    /**
     * @return The tax exemption reason if provided
     */
    @JsonIgnore
    public Optional<String> getTaxExemptionReason() {
        return taxExemptionReason != null ? taxExemptionReason : Optional.empty();
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TaxSubTotal && equalTo((TaxSubTotal) other);
    }

    private boolean equalTo(TaxSubTotal other) {
        return Objects.equals(this.taxableAmount, other.taxableAmount)
                && Objects.equals(this.taxAmount, other.taxAmount)
                && Objects.equals(this.taxCategoryId, other.taxCategoryId)
                && Objects.equals(this.percent, other.percent)
                && Objects.equals(this.taxScheme, other.taxScheme)
                && Objects.equals(this.taxExemptionReasonCode, other.taxExemptionReasonCode)
                && Objects.equals(this.taxExemptionReason, other.taxExemptionReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.taxableAmount, this.taxAmount, this.taxCategoryId,
                this.percent, this.taxScheme, this.taxExemptionReasonCode, this.taxExemptionReason);
    }

    @Override
    public String toString() {
        return "TaxSubTotal{" +
                "taxableAmount=" + taxableAmount +
                ", taxAmount=" + taxAmount +
                ", taxCategoryId='" + taxCategoryId + '\'' +
                ", percent=" + percent +
                ", taxScheme=" + taxScheme +
                ", taxExemptionReasonCode=" + taxExemptionReasonCode +
                ", taxExemptionReason=" + taxExemptionReason +
                '}';
    }

    public static TaxSubTotal.Builder builder() {
        return new TaxSubTotal.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private BigDecimal taxableAmount;
        private BigDecimal taxAmount;
        private String taxCategoryId;
        private Optional<Double> percent = Optional.empty();
        private Optional<String> taxScheme = Optional.empty();
        private Optional<String> taxExemptionReasonCode = Optional.empty();
        private Optional<String> taxExemptionReason = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public TaxSubTotal.Builder from(TaxSubTotal other) {
            taxableAmount(other.getTaxableAmount());
            taxAmount(other.getTaxAmount());
            taxCategoryId(other.getTaxCategoryId());
            percent(other.getPercent().orElse(null));
            taxScheme(other.getTaxScheme().orElse(null));
            taxExemptionReasonCode(other.getTaxExemptionReasonCode().orElse(null));
            taxExemptionReason(other.getTaxExemptionReason().orElse(null));
            return this;
        }

        @JsonSetter(value = "taxableAmount", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder taxableAmount(BigDecimal taxableAmount) {
            this.taxableAmount = taxableAmount;
            return this;
        }

        @JsonSetter(value = "taxAmount", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder taxAmount(BigDecimal taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        @JsonSetter(value = "taxCategoryId", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder taxCategoryId(String taxCategoryId) {
            this.taxCategoryId = taxCategoryId;
            return this;
        }

        @JsonSetter(value = "percent", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder percent(Double percent) {
            this.percent = Optional.ofNullable(percent);
            return this;
        }

        public TaxSubTotal.Builder percent(Optional<Double> percent) {
            this.percent = percent != null ? percent : Optional.empty();
            return this;
        }

        @JsonSetter(value = "taxScheme", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder taxScheme(String taxScheme) {
            this.taxScheme = Optional.ofNullable(taxScheme);
            return this;
        }

        public TaxSubTotal.Builder taxScheme(Optional<String> taxScheme) {
            this.taxScheme = taxScheme != null ? taxScheme : Optional.empty();
            return this;
        }

        @JsonSetter(value = "taxExemptionReasonCode", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder taxExemptionReasonCode(String taxExemptionReasonCode) {
            this.taxExemptionReasonCode = Optional.ofNullable(taxExemptionReasonCode);
            return this;
        }

        public TaxSubTotal.Builder taxExemptionReasonCode(Optional<String> taxExemptionReasonCode) {
            this.taxExemptionReasonCode = taxExemptionReasonCode != null ? taxExemptionReasonCode : Optional.empty();
            return this;
        }

        @JsonSetter(value = "taxExemptionReason", nulls = Nulls.SKIP)
        public TaxSubTotal.Builder taxExemptionReason(String taxExemptionReason) {
            this.taxExemptionReason = Optional.ofNullable(taxExemptionReason);
            return this;
        }

        public TaxSubTotal.Builder taxExemptionReason(Optional<String> taxExemptionReason) {
            this.taxExemptionReason = taxExemptionReason != null ? taxExemptionReason : Optional.empty();
            return this;
        }

        public TaxSubTotal build() {
            return new TaxSubTotal(taxableAmount, taxAmount, taxCategoryId, percent,
                    taxScheme, taxExemptionReasonCode, taxExemptionReason, additionalProperties);
        }
    }

    /**
     * Convenience method to get validation errors.
     * Returns a list of error messages if the tax subtotal has invalid mandatory fields.
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Check mandatory fields
        if (taxableAmount == null) {
            errors.add("Taxable amount is required");
        }

        if (taxAmount == null) {
            errors.add("Tax amount is required");
        }

        if (taxCategoryId == null || taxCategoryId.trim().isEmpty()) {
            errors.add("Tax category ID is required");
        }

        return errors;
    }
}