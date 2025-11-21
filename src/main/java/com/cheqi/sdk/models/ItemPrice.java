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
 * Item price DTO representing pricing information for products.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>priceAmount</strong>: The unit price amount</li>
 *   <li><strong>baseQuantity</strong>: The base quantity for pricing</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>allowanceCharge</strong>: Any allowances or charges applied to the price</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = ItemPrice.Builder.class)
public final class ItemPrice {
    // ===== MANDATORY FIELDS =====

    /**
     * The unit price amount.
     */
    @JsonProperty("unitPrice")
    private final BigDecimal priceAmount;

    /**
     * The base quantity for which the price is specified.
     */
    @JsonProperty("baseQuantity")
    private final Quantity baseQuantity;

    // ===== OPTIONAL FIELDS =====

    /**
     * Any allowances or charges applied to the price.
     */
    @JsonProperty("allowanceCharges")
    private final Optional<AllowanceCharge> allowanceCharge;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private ItemPrice(
            BigDecimal priceAmount,
            Quantity baseQuantity,
            Optional<AllowanceCharge> allowanceCharge,
            Map<String, Object> additionalProperties) {
        this.priceAmount = priceAmount;
        this.baseQuantity = baseQuantity;
        this.allowanceCharge = allowanceCharge;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    @JsonIgnore
    public Quantity getBaseQuantity() {
        return baseQuantity;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The allowance charge if provided
     */
    @JsonIgnore
    public Optional<AllowanceCharge> getAllowanceCharge() {
        return allowanceCharge != null ? allowanceCharge : Optional.empty();
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ItemPrice && equalTo((ItemPrice) other);
    }

    private boolean equalTo(ItemPrice other) {
        return Objects.equals(this.priceAmount, other.priceAmount)
                && Objects.equals(this.baseQuantity, other.baseQuantity)
                && Objects.equals(this.allowanceCharge, other.allowanceCharge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.priceAmount, this.baseQuantity, this.allowanceCharge);
    }

    @Override
    public String toString() {
        return "ItemPrice{" +
                "priceAmount=" + priceAmount +
                ", baseQuantity=" + baseQuantity +
                ", allowanceCharge=" + allowanceCharge +
                '}';
    }

    public static ItemPrice.Builder builder() {
        return new ItemPrice.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private BigDecimal priceAmount;
        private Quantity baseQuantity;
        private Optional<AllowanceCharge> allowanceCharge = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public ItemPrice.Builder from(ItemPrice other) {
            priceAmount(other.getPriceAmount());
            baseQuantity(other.getBaseQuantity());
            allowanceCharge(other.getAllowanceCharge().orElse(null));
            return this;
        }

        @JsonSetter(value = "unitPrice", nulls = Nulls.SKIP)
        public ItemPrice.Builder priceAmount(BigDecimal priceAmount) {
            this.priceAmount = priceAmount;
            return this;
        }

        @JsonSetter(value = "baseQuantity", nulls = Nulls.SKIP)
        public ItemPrice.Builder baseQuantity(Quantity baseQuantity) {
            this.baseQuantity = baseQuantity;
            return this;
        }

        @JsonSetter(value = "allowanceCharges", nulls = Nulls.SKIP)
        public ItemPrice.Builder allowanceCharge(AllowanceCharge allowanceCharge) {
            this.allowanceCharge = Optional.ofNullable(allowanceCharge);
            return this;
        }

        public ItemPrice.Builder allowanceCharge(Optional<AllowanceCharge> allowanceCharge) {
            this.allowanceCharge = allowanceCharge != null ? allowanceCharge : Optional.empty();
            return this;
        }

        public ItemPrice build() {
            return new ItemPrice(priceAmount, baseQuantity, allowanceCharge, additionalProperties);
        }
    }

    // ===== VALIDATION METHODS =====

    /**
     * Validates this item price.
     * @return true if all mandatory fields are present and valid
     */
    public boolean isValid() {
        List<String> errors = getValidationErrors();
        return errors.isEmpty();
    }

    /**
     * Gets detailed validation errors for this item price.
     * @return List of validation error messages, empty if valid
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Mandatory field validation
        if (priceAmount == null) {
            errors.add("Price amount is required");
        } else if (priceAmount.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Price amount cannot be negative");
        }

        if (baseQuantity == null) {
            errors.add("Base quantity is required");
        } else {
            // Cascade validation to child DTOs
            List<String> quantityErrors = baseQuantity.getValidationErrors();
            for (String error : quantityErrors) {
                errors.add("Base quantity: " + error);
            }
        }

        if (allowanceCharge != null && allowanceCharge.isPresent()) {
            AllowanceCharge charge = allowanceCharge.get();
            if (charge != null) {
                List<String> chargeErrors = charge.getValidationErrors();
                for (String error : chargeErrors) {
                    errors.add("Allowance charge: " + error);
                }
            }
        }

        return errors;
    }
}
