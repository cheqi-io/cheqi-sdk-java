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
 * Quantity DTO representing quantity and unit information for products.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>quantity</strong>: The numeric quantity value</li>
 *   <li><strong>unitCode</strong>: Unit of measure code (e.g., "C62" for pieces)</li>
 * </ul>
 *
 * <h3>Unit Codes:</h3>
 * <p>Unit codes should follow the UN/ECE Recommendation 20 standard.</p>
 * <p>Common codes: C62 (pieces), KGM (kilograms), LTR (liters), MTR (meters)</p>
 *
 * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNECERec20/">UN/ECE Rec 20 Unit Codes</a>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Quantity.Builder.class)
public final class Quantity {

    // ===== MANDATORY FIELDS =====

    /**
     * The numeric quantity value.
     */
    @JsonProperty("quantity")
    private final BigDecimal quantity;

    /**
     * Unit of measure code following UN/ECE Recommendation 20.
     * Common examples: C62 (pieces), KGM (kilograms), LTR (liters), MTR (meters).
     */
    @JsonProperty("unitCode")
    private final String unitCode;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private Quantity(
            BigDecimal quantity,
            String unitCode,
            Map<String, Object> additionalProperties) {
        this.quantity = quantity;
        this.unitCode = unitCode;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public BigDecimal getQuantity() {
        return quantity;
    }

    @JsonIgnore
    public String getUnitCode() {
        return unitCode;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Quantity && equalTo((Quantity) other);
    }

    private boolean equalTo(Quantity other) {
        return Objects.equals(this.quantity, other.quantity)
                && Objects.equals(this.unitCode, other.unitCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.quantity, this.unitCode);
    }

    @Override
    public String toString() {
        return "Quantity{" +
                "quantity=" + quantity +
                ", unitCode='" + unitCode + '\'' +
                '}';
    }

    public static Quantity.Builder builder() {
        return new Quantity.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private BigDecimal quantity;
        private String unitCode;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Quantity.Builder from(Quantity other) {
            quantity(other.getQuantity());
            unitCode(other.getUnitCode());
            return this;
        }

        @JsonSetter(value = "quantity", nulls = Nulls.SKIP)
        public Quantity.Builder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        @JsonSetter(value = "unitCode", nulls = Nulls.SKIP)
        public Quantity.Builder unitCode(String unitCode) {
            this.unitCode = unitCode;
            return this;
        }

        public Quantity build() {
            return new Quantity(quantity, unitCode, additionalProperties);
        }
    }

    // ===== VALIDATION METHODS =====

    /**
     * Validates this quantity.
     * @return true if all mandatory fields are present and valid
     */
    public boolean isValid() {
        List<String> errors = getValidationErrors();
        return errors.isEmpty();
    }

    /**
     * Gets detailed validation errors for this quantity.
     * @return List of validation error messages, empty if valid
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Mandatory field validation
        if (quantity == null) {
            errors.add("Quantity is required");
        } else if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Quantity must be greater than zero");
        }

        if (unitCode == null || unitCode.trim().isEmpty()) {
            errors.add("Unit code is required (see UN/ECE Recommendation 20, e.g., 'C62' for pieces)");
        }

        return errors;
    }
}