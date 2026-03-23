package com.cheqi.sdk.models;

import com.cheqi.sdk.models.generated.BarcodeType;

/**
 * A barcode or QR code to embed in a receipt.
 * Extends the generated base with factory methods and BarcodeType enum support.
 *
 * <pre>
 * Barcode returnCode = Barcode.qrCode("https://example.com/return/12345", "Return code");
 * Barcode loyaltyCode = Barcode.of(BarcodeType.CODE_128, "LOYALTY-ABC-123", "Loyalty points");
 * </pre>
 */
public class Barcode extends com.cheqi.sdk.models.generated.Barcode {

    public Barcode() {}

    // ===== FACTORY METHODS =====

    public static Barcode of(BarcodeType type, String data, String label) {
        Barcode b = new Barcode();
        b.setType(type.name());
        b.setData(data);
        b.setLabel(label);
        return b;
    }

    public static Barcode qrCode(String data, String label) {
        return of(BarcodeType.QR_CODE, data, label);
    }

    public static Barcode qrCode(String data) {
        return of(BarcodeType.QR_CODE, data, null);
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BarcodeType type;
        private String data;
        private String label;

        private Builder() {}

        public Builder type(BarcodeType type) {
            this.type = type;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

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
            Barcode b = new Barcode();
            b.setType(type.name());
            b.setData(data);
            b.setLabel(label);
            return b;
        }
    }
}
