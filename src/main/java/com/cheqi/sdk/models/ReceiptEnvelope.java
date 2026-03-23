package com.cheqi.sdk.models;

import com.cheqi.sdk.models.generated.CheqiReceipt;
import com.cheqi.sdk.models.generated.VatMetadata;

/**
 * Envelope containing multiple receipt formats.
 * Used to bundle different format representations of the same receipt
 * into a single encrypted payload.
 */
public class ReceiptEnvelope {
    private CheqiReceipt cheqiReceipt;  // CHEQI JSON format (null if not requested)
    private String ublXml;        // UBL XML format (null if not requested)
    private VatMetadata vatMetadata;

    public ReceiptEnvelope() {
    }

    public ReceiptEnvelope(CheqiReceipt cheqiReceipt, String ublXml) {
        this.cheqiReceipt = cheqiReceipt;
        this.ublXml = ublXml;
    }

    public CheqiReceipt getCheqiReceipt() {
        return cheqiReceipt;
    }

    public void setCheqiReceipt(CheqiReceipt cheqiReceipt) {
        this.cheqiReceipt = cheqiReceipt;
    }

    public String getUblXml() {
        return ublXml;
    }

    public void setUblXml(String ublXml) {
        this.ublXml = ublXml;
    }

    public VatMetadata getVatMetadata() {
        return vatMetadata;
    }

    public void setVatMetadata(VatMetadata vatMetadata) {
        this.vatMetadata = vatMetadata;
    }
}
