package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

/**
 * A barcode or QR code to embed in a receipt.
 *
 * <p>Barcodes are rendered on the customer's device and can be used for returns,
 * loyalty programs, tickets, or any other scannable code.</p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * Barcode returnCode = Barcode.qrCode("https://example.com/return/12345", "Return code");
 * Barcode loyaltyCode = Barcode.of(BarcodeType.CODE_128, "LOYALTY-ABC-123", "Loyalty points");
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Barcode.Builder.class)
public final class Barcode {

    /**
     * The type of barcode (e.g., QR_CODE, EAN_13, CODE_128).
     */
    @JsonProperty("type")
    private final BarcodeType type;

    /**
     * The data encoded in the barcode.
     */
    @JsonProperty("data")
    private final String data;

    /**
     * A human-readable label displayed alongside the barcode (e.g., "Return code").
     */
    @JsonProperty("label")
    private final String label;

    private Barcode(BarcodeType type, String data, String label) {
        this.type = type;
        this.data = data;
        this.label = label;
    }

    // ===== GETTERS =====

    public BarcodeType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getLabel() {
        return label;
    }

    // ===== FACTORY METHODS =====

    /**
     * Creates a barcode with the given type, data, and label.
     *
     * @param type  The barcode type
     * @param data  The data to encode
     * @param label A human-readable label (optional, can be null)
     * @return A new Barcode instance
     */
    public static Barcode of(BarcodeType type, String data, String label) {
        return new Barcode(type, data, label);
    }

    /**
     * Creates a QR code barcode.
     *
     * @param data  The data to encode in the QR code
     * @param label A human-readable label
     * @return A new QR code Barcode instance
     */
    public static Barcode qrCode(String data, String label) {
        return new Barcode(BarcodeType.QR_CODE, data, label);
    }

    /**
     * Creates a QR code barcode without a label.
     *
     * @param data The data to encode in the QR code
     * @return A new QR code Barcode instance
     */
    public static Barcode qrCode(String data) {
        return new Barcode(BarcodeType.QR_CODE, data, null);
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private BarcodeType type;
        private String data;
        private String label;

        private Builder() {}

        @JsonSetter(value = "type", nulls = Nulls.SKIP)
        public Builder type(BarcodeType type) {
            this.type = type;
            return this;
        }

        @JsonSetter(value = "data", nulls = Nulls.SKIP)
        public Builder data(String data) {
            this.data = data;
            return this;
        }

        @JsonSetter(value = "label", nulls = Nulls.SKIP)
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Barcode build() {
            if (type == null) {
                throw new IllegalStateException("Barcode type is required");
            }
            if (data == null || data.isEmpty()) {
                throw new IllegalStateException("Barcode data is required");
            }
            return new Barcode(type, data, label);
        }
    }

    // ===== EQUALITY =====

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Barcode)) return false;
        Barcode that = (Barcode) other;
        return type == that.type
                && Objects.equals(data, that.data)
                && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, data, label);
    }

    @Override
    public String toString() {
        return "Barcode{" +
                "type=" + type +
                ", data='" + data + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
