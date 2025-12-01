package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a merchant-defined identifier such as:
 * - order number
 * - webshop ID
 * - POS ticket number
 * - customer reference
 * - ERP reference
 * - shipping reference
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Identifier {

    @JsonProperty("type")
    private final String type;

    @JsonProperty("value")
    private final String value;

    public Identifier(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    // Convenience builder
    public static Identifier of(String type, String value) {
        return new Identifier(type, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "Identifier{type='" + type + "', value='" + value + "'}";
    }
}
