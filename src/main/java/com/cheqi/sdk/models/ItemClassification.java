package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Item classification DTO representing product classification information.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>listId</strong>: The classification scheme identifier</li>
 *   <li><strong>value</strong>: The classification code/value within the scheme</li>
 * </ul>
 *
 * <h3>Common Classification Schemes:</h3>
 * <ul>
 *   <li><strong>EAN</strong>: European Article Number (barcode standard)</li>
 *   <li><strong>GTIN</strong>: Global Trade Item Number (includes EAN, UPC, etc.)</li>
 *   <li><strong>UPC</strong>: Universal Product Code</li>
 *   <li><strong>UNSPSC</strong>: United Nations Standard Products and Services Code</li>
 *   <li><strong>CPV</strong>: Common Procurement Vocabulary</li>
 *   <li><strong>CAS</strong>: Chemical Abstracts Service Registry Number</li>
 *   <li><strong>ISBN</strong>: International Standard Book Number</li>
 *   <li><strong>ISSN</strong>: International Standard Serial Number</li>
 * </ul>
 *
 * <h3>Examples:</h3>
 * <ul>
 *   <li>EAN: listId="EAN", value="1234567890123"</li>
 *   <li>GTIN: listId="GTIN", value="01234567890128"</li>
 *   <li>UNSPSC: listId="UNSPSC", value="12345678"</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = ItemClassification.Builder.class)
public final class ItemClassification {

    // ===== MANDATORY FIELDS =====

    /**
     * The classification scheme identifier.
     * Examples: "UNSPSC", "CPV", "GTIN", "CAS"
     */
    @JsonProperty("listId")
    private final String listId;

    /**
     * The classification code/value within the specified scheme.
     * The format depends on the classification scheme used.
     */
    @JsonProperty("value")
    private final String value;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private ItemClassification(
            String listId,
            String value,
            Map<String, Object> additionalProperties) {
        this.listId = listId;
        this.value = value;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public String getListId() {
        return listId;
    }

    @JsonIgnore
    public String getValue() {
        return value;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ItemClassification && equalTo((ItemClassification) other);
    }

    private boolean equalTo(ItemClassification other) {
        return Objects.equals(this.listId, other.listId)
                && Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.listId, this.value);
    }

    @Override
    public String toString() {
        return "ItemClassification{" +
                "listId='" + listId + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static ItemClassification.Builder builder() {
        return new ItemClassification.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String listId;
        private String value;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public ItemClassification.Builder from(ItemClassification other) {
            listId(other.getListId());
            value(other.getValue());
            return this;
        }

        @JsonSetter(value = "listId", nulls = Nulls.SKIP)
        public ItemClassification.Builder listId(String listId) {
            this.listId = listId;
            return this;
        }

        @JsonSetter(value = "value", nulls = Nulls.SKIP)
        public ItemClassification.Builder value(String value) {
            this.value = value;
            return this;
        }

        public ItemClassification build() {
            return new ItemClassification(listId, value, additionalProperties);
        }
    }

}
