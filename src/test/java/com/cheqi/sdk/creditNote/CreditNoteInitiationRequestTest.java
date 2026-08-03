package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditNoteInitiationRequestTest {
    @Test
    void serializesPerProductReasonsWithoutLegacyAggregateReason() throws Exception {
        CreditNoteInitiationRequest request = CreditNoteInitiationRequest.builder()
                .cheqiReceiptId("CHQ-1")
                .receiptId("merchant-receipt-1")
                .customerNote("Please process both items")
                .lineItems(List.of(
                        new ReturnLineItem("SKU-1", new BigDecimal("0.75"), ReturnReasonCode.DAMAGED, "Seal broken"),
                        new ReturnLineItem("SKU-1", new BigDecimal("0.25"), ReturnReasonCode.UNWANTED, null)
                ))
                .refundPreference(RefundPreference.ORIGINAL_PAYMENT_METHOD)
                .build();

        String json = new ObjectMapper().writeValueAsString(request);

        assertTrue(json.contains("\"productId\":\"SKU-1\""));
        assertTrue(json.contains("\"quantity\":0.75"));
        assertTrue(json.contains("\"reasonCode\":\"DAMAGED\""));
        assertFalse(json.contains("returnReason"));
        assertEquals(2, request.getLineItems().size());
    }

    @Test
    void rejectsInvalidPlaintextBeforeItCanBeEncrypted() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> CreditNoteInitiationRequest.builder()
                        .cheqiReceiptId("CHQ-1")
                        .receiptId("merchant-receipt-1")
                        .lineItems(List.of(
                                new ReturnLineItem("SKU-1", BigDecimal.ZERO, ReturnReasonCode.DAMAGED, null)
                        ))
                        .refundPreference(RefundPreference.ORIGINAL_PAYMENT_METHOD)
                        .build()
        );

        assertEquals("lineItems.quantity must be greater than zero", error.getMessage());
    }

    @Test
    void bankTransferRequiresBankDetails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CreditNoteInitiationRequest.builder()
                        .cheqiReceiptId("CHQ-1")
                        .receiptId("merchant-receipt-1")
                        .lineItems(List.of(
                                new ReturnLineItem("SKU-1", BigDecimal.ONE, ReturnReasonCode.UNWANTED, null)
                        ))
                        .refundPreference(RefundPreference.BANK_TRANSFER)
                        .build()
        );
    }
}
