package com.cheqi.sdk.models;

/**
 * Supported receipt formats for delivery and API access.
 * The system maintains a canonical CheqiReceipt format (JSON) and transforms
 * to other standard formats as needed.
 */
public enum ReceiptFormat {
    /**
     * CheqiReceipt canonical format (JSON) - System default
     * Simplest, most efficient format for internal use
     */
    CHEQI("cheqi", "application/json"),

    /**
     * Universal Business Language (UBL) 2.1 XML format
     * Standard for B2B invoicing and receipts
     */
    UBL_XML("ubl", "application/xml");

    private final String code;
    private final String mimeType;

    ReceiptFormat(String code, String mimeType) {
        this.code = code;
        this.mimeType = mimeType;
    }

    /**
     * Get the format code used in API requests and responses
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the MIME type for HTTP Content-Type header
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Parse format from string code
     */
    public static ReceiptFormat fromCode(String code) {
        for (ReceiptFormat format : values()) {
            if (format.code.equalsIgnoreCase(code)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown format code: " + code);
    }
}
