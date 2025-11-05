package com.cheqi.sdk.matching;

public class CustomerIdentifier {
    private final IdentifierType type;
    private final String value;

    private CustomerIdentifier(Builder builder) {
        this.type = builder.type;
        this.value = builder.value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public IdentifierType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {
        private IdentifierType type;
        private String value;

        public Builder type(IdentifierType type) {
            this.type = type;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

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
