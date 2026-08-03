package com.cheqi.sdk.models;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.FiscalizationStatus;
import com.cheqi.sdk.models.generated.PaymentDetails;
import com.cheqi.sdk.models.generated.UnitCode;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReceiptPayloadTest {
    @Test
    void serializesCallerSuppliedValuesAndJurisdictionDataWithoutCalculations() throws Exception {
        JurisdictionalData jurisdiction = JurisdictionalData.builder()
                .countryCode("AT")
                .profile("AT-RKSV")
                .profileVersion("1")
                .fiscalization(FiscalizationData.of("RKSV", FiscalizationStatus.FISCALIZED)
                        .addField("signatureValue", "issuer-supplied-signature", "Signature"))
                .addLegalText("cash-register-notice", "Issuer supplied text", "de")
                .addField("receiptType", "STANDARD", "Receipt type")
                .build();

        ReceiptPayload payload = ReceiptPayload.builder()
                .documentNumber("AT-100")
                .issueDate(OffsetDateTime.parse("2026-07-29T12:00:00+02:00"))
                .currency("EUR")
                .receiptSubtotal("7.01")
                .totalBeforeTax("8.02")
                .totalTaxAmount("3.03")
                .totalAmount("9.04")
                .taxesApplied(true)
                .paymentDetails(new PaymentDetails()
                        .paymentMeansCode("48")
                        .cardLastFour("4242")
                        .merchantId("MID-EXACT")
                        .paymentTerminalId("TID-EXACT"))
                .addProduct(Product.builder()
                        .name("Supplied line")
                        .identifier("LINE-1")
                        .quantity(2.0)
                        .unitCode(UnitCode.C62)
                        .unitPrice("1.11")
                        .subtotal("2.22")
                        .total("3.33")
                        .build())
                .addQrCode("https://verify.example/receipts/AT-100?token=long-value", "Verify")
                .jurisdictionalData(jurisdiction)
                .build();

        JsonNode json = ObjectMapperConfig.getInstance().valueToTree(payload);

        assertEquals("7.01", json.get("receiptSubtotal").asText());
        assertEquals("8.02", json.get("totalBeforeTax").asText());
        assertEquals("3.03", json.get("totalTaxAmount").asText());
        assertEquals("9.04", json.get("totalAmount").asText());
        assertEquals("48", json.at("/paymentDetails/paymentMeansCode").asText());
        assertEquals("4242", json.at("/paymentDetails/cardLastFour").asText());
        assertEquals("MID-EXACT", json.at("/paymentDetails/merchantId").asText());
        assertEquals("TID-EXACT", json.at("/paymentDetails/paymentTerminalId").asText());
        assertEquals("3.33", json.at("/products/0/total").asText());
        assertEquals("AT", json.at("/jurisdictionalData/countryCode").asText());
        assertEquals("issuer-supplied-signature",
                json.at("/jurisdictionalData/fiscalization/additionalFields/0/value").asText());
        assertEquals("https://verify.example/receipts/AT-100?token=long-value",
                json.at("/barcodes/0/data").asText());
        assertFalse(json.has("receiptTemplateRequest"));
    }
}
