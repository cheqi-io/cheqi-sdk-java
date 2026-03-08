package com.cheqi.sdk.models;

/**
 * Supported barcode and scannable code types.
 *
 * <p>These types determine how the barcode data is encoded and rendered
 * on the customer's device.</p>
 */
public enum BarcodeType {
    /** QR code — square 2D barcode, supports URLs and arbitrary text */
    QR_CODE,
    /** EAN-13 — standard European/international product barcode (13 digits) */
    EAN_13,
    /** EAN-8 — compact European product barcode (8 digits) */
    EAN_8,
    /** Code 128 — high-density linear barcode, supports full ASCII */
    CODE_128,
    /** UPC-A — standard North American product barcode (12 digits) */
    UPC_A,
    /** Data Matrix — compact square 2D barcode */
    DATA_MATRIX
}
