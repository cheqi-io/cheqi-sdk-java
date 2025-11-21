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
 * Tax rate DTO representing tax information for products or services.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>id</strong>: Tax category ID (defaults to "S" for standard rate)</li>
 *   <li><strong>taxScheme</strong>: Tax scheme code (defaults to "VAT")</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>percentage</strong>: Tax rate percentage</li>
 *   <li><strong>taxName</strong>: Human-readable tax name</li>
 *   <li><strong>totalTaxAmount</strong>: Total tax amount for this rate</li>
 * </ul>
 *
 * <h3>Tax Category IDs (UNCL5305):</h3>
 * <ul>
 *   <li><strong>S</strong>: Standard rate (default)</li>
 *   <li><strong>Z</strong>: Zero rated</li>
 *   <li><strong>E</strong>: Exempt from tax</li>
 *   <li><strong>AE</strong>: VAT Reverse Charge</li>
 *   <li><strong>K</strong>: VAT exempt for EEA intra-community supply</li>
 *   <li><strong>G</strong>: Free export item, tax not charged</li>
 * </ul>
 *
 * <h3>Common Tax Schemes:</h3>
 * <ul>
 *   <li><strong>VAT</strong>: Value Added Tax (default)</li>
 *   <li><strong>GST</strong>: Goods and Services Tax</li>
 *   <li><strong>AAA</strong>: Petroleum tax</li>
 *   <li><strong>AAB</strong>: Provisional countervailing duty cash</li>
 * </ul>
 *
 * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL5305/">UNCL5305 Tax Category Codes</a>
 * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/syntax/ubl-invoice/cac-InvoiceLine/cac-Item/cac-ClassifiedTaxCategory/cac-TaxScheme/">Tax Scheme Reference</a>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = TaxRate.Builder.class)
public final class TaxRate {

    // ===== MANDATORY FIELDS =====
    /**
     * Tax category ID according to UNCL5305.
     * Defaults to "S" (standard rate) if not specified.
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL5305/">UNCL5305 Tax Category Codes</a>
     */
    @JsonProperty("id")
    private final String id;

    /**
     * Tax scheme code that represents the tax scheme applied.
     * Defaults to "VAT" if not specified.
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/syntax/ubl-invoice/cac-InvoiceLine/cac-Item/cac-ClassifiedTaxCategory/cac-TaxScheme/">Tax Scheme Reference</a>
     */
    @JsonProperty("taxScheme")
    private final String taxScheme;

    // ===== OPTIONAL FIELDS =====

    /**
     * Tax rate percentage for the given item.
     * Example: 21.0 for 21% VAT rate.
     */
    @JsonProperty("percentage")
    private final Optional<Double> percentage;

    /**
     * Human-readable tax name.
     * Examples: "VAT", "Sales Tax", "GST"
     */
    @JsonProperty("taxName")
    private final Optional<String> taxName;

    /**
     * Total tax amount calculated for this tax rate.
     */
    @JsonProperty("totalTaxAmount")
    private final Optional<BigDecimal> totalTaxAmount;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private TaxRate(
            String id,
            String taxScheme,
            Optional<Double> percentage,
            Optional<String> taxName,
            Optional<BigDecimal> totalTaxAmount,
            Map<String, Object> additionalProperties) {
        this.id = id;
        this.taxScheme = taxScheme;
        this.percentage = percentage;
        this.taxName = taxName;
        this.totalTaxAmount = totalTaxAmount;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public String getTaxScheme() {
        return taxScheme;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The tax percentage if provided
     */
    @JsonIgnore
    public Optional<Double> getPercentage() {
        return percentage != null ? percentage : Optional.empty();
    }

    /**
     * @return The tax name if provided
     */
    @JsonIgnore
    public Optional<String> getTaxName() {
        return taxName != null ? taxName : Optional.empty();
    }

    /**
     * @return The total tax amount if provided
     */
    @JsonIgnore
    public Optional<BigDecimal> getTotalTaxAmount() {
        return totalTaxAmount != null ? totalTaxAmount : Optional.empty();
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TaxRate && equalTo((TaxRate) other);
    }

    private boolean equalTo(TaxRate other) {
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.taxScheme, other.taxScheme)
                && Objects.equals(this.percentage, other.percentage)
                && Objects.equals(this.taxName, other.taxName)
                && Objects.equals(this.totalTaxAmount, other.totalTaxAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.taxScheme, this.percentage, this.taxName, this.totalTaxAmount);
    }

    @Override
    public String toString() {
        return "TaxRate{" +
                "id='" + id + '\'' +
                ", taxScheme='" + taxScheme + '\'' +
                ", percentage=" + percentage +
                ", taxName=" + taxName +
                ", totalTaxAmount=" + totalTaxAmount +
                '}';
    }

    public static TaxRate.Builder builder() {
        return new TaxRate.Builder();
    }

    /**
     * Creates a TaxRate with just the percentage (using defaults for id and taxScheme).
     * Convenience method for simple tax rate creation.
     */
    public static TaxRate withPercentage(Double percentage) {
        return builder()
                .percentage(percentage)
                .build();
    }

    /**
     * Creates a TaxRate with id and percentage (using default for taxScheme).
     * Convenience method for tax rate creation with specific category.
     */
    public static TaxRate withIdAndPercentage(String id, Double percentage) {
        return builder()
                .id(id)
                .percentage(percentage)
                .build();
    }

    /**
     * Creates a TaxRate with all main fields.
     * Convenience method for complete tax rate creation.
     */
    public static TaxRate create(String id, String taxScheme, Double percentage) {
        return builder()
                .id(id)
                .taxScheme(taxScheme)
                .percentage(percentage)
                .build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private String taxScheme;
        private Optional<Double> percentage = Optional.empty();
        private Optional<String> taxName = Optional.empty();
        private Optional<BigDecimal> totalTaxAmount = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public TaxRate.Builder from(TaxRate other) {
            id(other.getId());
            taxScheme(other.getTaxScheme());
            percentage(other.getPercentage().orElse(null));
            taxName(other.getTaxName().orElse(null));
            totalTaxAmount(other.getTotalTaxAmount().orElse(null));
            return this;
        }

        @JsonSetter(value = "id", nulls = Nulls.SKIP)
        public TaxRate.Builder id(String id) {
            this.id = id;
            return this;
        }

        @JsonSetter(value = "taxScheme", nulls = Nulls.SKIP)
        public TaxRate.Builder taxScheme(String taxScheme) {
            this.taxScheme = taxScheme;
            return this;
        }

        @JsonSetter(value = "percentage", nulls = Nulls.SKIP)
        public TaxRate.Builder percentage(Double percentage) {
            this.percentage = Optional.ofNullable(percentage);
            return this;
        }

        public TaxRate.Builder percentage(double percentage) {
            this.percentage = Optional.of(percentage);
            return this;
        }

        public TaxRate.Builder percentage(Optional<Double> percentage) {
            this.percentage = percentage != null ? percentage : Optional.empty();
            return this;
        }

        @JsonSetter(value = "taxName", nulls = Nulls.SKIP)
        public TaxRate.Builder taxName(String taxName) {
            this.taxName = Optional.ofNullable(taxName);
            return this;
        }

        public TaxRate.Builder taxName(Optional<String> taxName) {
            this.taxName = taxName != null ? taxName : Optional.empty();
            return this;
        }

        @JsonSetter(value = "totalTaxAmount", nulls = Nulls.SKIP)
        public TaxRate.Builder totalTaxAmount(BigDecimal totalTaxAmount) {
            this.totalTaxAmount = Optional.ofNullable(totalTaxAmount);
            return this;
        }

        public TaxRate.Builder totalTaxAmount(Optional<BigDecimal> totalTaxAmount) {
            this.totalTaxAmount = totalTaxAmount != null ? totalTaxAmount : Optional.empty();
            return this;
        }

        public TaxRate build() {
            // Apply defaults for mandatory fields
            String finalId = id != null ? id : "S";
            String finalTaxScheme = taxScheme != null ? taxScheme : "VAT";

            return new TaxRate(finalId, finalTaxScheme, percentage, taxName, totalTaxAmount, additionalProperties);
        }
    }

    /**
     * Convenience method to get validation errors.
     * Returns a list of error messages if the tax rate has invalid mandatory fields.
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Check mandatory fields
        if (id == null || id.trim().isEmpty()) {
            errors.add("Tax category ID is required and cannot be blank");
        }

        if (taxScheme == null || taxScheme.trim().isEmpty()) {
            errors.add("Tax scheme is required and cannot be blank");
        }

        return errors;
    }
}