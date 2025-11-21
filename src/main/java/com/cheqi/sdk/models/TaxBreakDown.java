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

/**
 * Tax breakdown DTO representing tax totals and detailed tax subtotals.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>taxTotal</strong>: The total tax amount for all tax categories</li>
 *   <li><strong>taxSubtotals</strong>: List of detailed tax subtotals by category/rate</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <p>This DTO provides both a summary (total tax amount) and detailed breakdown 
 * (individual tax subtotals by category, rate, or jurisdiction).</p>
 *
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>The taxTotal should equal the sum of all taxSubtotal amounts</li>
 *   <li>At least one tax subtotal must be provided</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = TaxBreakDown.Builder.class)
public final class TaxBreakDown {

    // ===== MANDATORY FIELDS =====

    /**
     * The total tax amount for all tax categories combined.
     * This should equal the sum of all tax subtotal amounts.
     */
    @JsonProperty("totalTaxAmount")
    private final BigDecimal taxTotal;

    /**
     * List of detailed tax subtotals broken down by category, rate, or jurisdiction.
     * Must contain at least one tax subtotal.
     */
    @JsonProperty("taxSubtotals")
    private final List<TaxSubTotal> taxSubtotals;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private TaxBreakDown(
            BigDecimal taxTotal,
            List<TaxSubTotal> taxSubtotals,
            Map<String, Object> additionalProperties) {
        this.taxTotal = taxTotal;
        this.taxSubtotals = taxSubtotals;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public BigDecimal getTaxTotal() {
        return taxTotal;
    }

    @JsonIgnore
    public List<TaxSubTotal> getTaxSubtotals() {
        return taxSubtotals;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TaxBreakDown && equalTo((TaxBreakDown) other);
    }

    private boolean equalTo(TaxBreakDown other) {
        return Objects.equals(this.taxTotal, other.taxTotal)
                && Objects.equals(this.taxSubtotals, other.taxSubtotals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.taxTotal, this.taxSubtotals);
    }

    @Override
    public String toString() {
        return "TaxBreakDown{" +
                "taxTotal=" + taxTotal +
                ", taxSubtotals=" + taxSubtotals +
                '}';
    }

    public static TaxBreakDown.Builder builder() {
        return new TaxBreakDown.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private BigDecimal taxTotal;
        private List<TaxSubTotal> taxSubtotals;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public TaxBreakDown.Builder from(TaxBreakDown other) {
            taxTotal(other.getTaxTotal());
            taxSubtotals(other.getTaxSubtotals());
            return this;
        }

        @JsonSetter(value = "totalTaxAmount", nulls = Nulls.SKIP)
        public TaxBreakDown.Builder taxTotal(BigDecimal taxTotal) {
            this.taxTotal = taxTotal;
            return this;
        }

        @JsonSetter(value = "taxSubtotals", nulls = Nulls.SKIP)
        public TaxBreakDown.Builder taxSubtotals(List<TaxSubTotal> taxSubtotals) {
            this.taxSubtotals = taxSubtotals;
            return this;
        }

        public TaxBreakDown build() {
            return new TaxBreakDown(taxTotal, taxSubtotals, additionalProperties);
        }
    }

    /**
     * Convenience method to get validation errors.
     * Returns a list of error messages if the tax breakdown has invalid mandatory fields.
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Check mandatory fields
        if (taxTotal == null) {
            errors.add("Tax total is required");
        }

        if (taxSubtotals == null) {
            errors.add("Tax subtotals list is required");
        } else if (taxSubtotals.isEmpty()) {
            errors.add("Tax subtotals list cannot be empty - at least one tax subtotal is required");
        } else {
            // Validate each tax subtotal in the list
            for (int i = 0; i < taxSubtotals.size(); i++) {
                TaxSubTotal subtotal = taxSubtotals.get(i);
                if (subtotal == null) {
                    errors.add("Tax subtotal at index " + i + " cannot be null");
                } else {
                    // Cascade validation to child DTOs
                    List<String> subtotalErrors = subtotal.getValidationErrors();
                    for (String subtotalError : subtotalErrors) {
                        errors.add("Tax subtotal at index " + i + ": " + subtotalError);
                    }
                }
            }
        }

        return errors;
    }
}
