package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CardProvider {
    MAESTRO,
    MASTERCARD,
    VISA,
    AMERICAN_EXPRESS;

    @JsonCreator
    public static CardProvider fromString(String value) {
        if (value == null) {
            return null;
        }

        // Normalize the input: convert to uppercase and remove spaces/underscores
        String normalized = value.toUpperCase()
                .replace(" ", "_")
                .replace("-", "_");

        // Handle common variations
        switch (normalized) {
            case "MASTERCARD":
            case "MASTER_CARD":
            case "MASTER":
                return MASTERCARD;
            case "VISA":
                return VISA;
            case "MAESTRO":
                return MAESTRO;
            case "AMERICAN_EXPRESS":
            case "AMERICANEXPRESS":
            case "AMERICAN EXPRESS":
            case "AMEX":
                return AMERICAN_EXPRESS;
            default:
                // Try exact match as fallback
                try {
                    return CardProvider.valueOf(normalized);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Unknown CardProvider: " + value +
                            ". Valid values are: VISA, MASTERCARD, MAESTRO, AMERICAN_EXPRESS");
                }
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
