package com.cheqi.sdk.matching;

/**
 * Represents a customer identifier used for matching.
 * 
 * Immutable value object containing an identifier type and value.
 * Use the builder pattern to create instances.
 */
public class CustomerIdentifier {
    private final IdentifierType type;
    private final String value;

    private CustomerIdentifier(Builder builder) {
        this.type = builder.type;
        this.value = builder.value;
    }

    /**
     * Creates a new builder for CustomerIdentifier.
     * 
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the identifier type.
     * 
     * @return The identifier type (PAR, PAN, IBAN, EMAIL)
     */
    public IdentifierType getType() {
        return type;
    }

    /**
     * Returns the identifier value.
     * 
     * @return The identifier value
     */
    public String getValue() {
        return value;
    }

    /**
     * Builder for CustomerIdentifier.
     */
    public static class Builder {
        private IdentifierType type;
        private String value;

        /**
         * Sets the identifier type.
         * 
         * @param type The identifier type (PAR, PAN, IBAN, EMAIL)
         * @return This builder instance
         */
        public Builder type(IdentifierType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the identifier value.
         * 
         * @param value The identifier value
         * @return This builder instance
         */
        public Builder value(String value) {
            this.value = value;
            return this;
        }

        /**
         * Builds the CustomerIdentifier.
         * 
         * @return A new CustomerIdentifier instance
         * @throws IllegalStateException if type or value is not set
         */
        public CustomerIdentifier build() {
            if (type == null) {
                throw new IllegalStateException("Identifier type must be set");
            }
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalStateException("Identifier value must be set and non-empty");
            }
            return new CustomerIdentifier(this);
        }
    }
}
