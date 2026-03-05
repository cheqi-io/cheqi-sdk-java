package com.cheqi.sdk.models;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptTemplateGenerationRequestTest {

    private final ObjectMapper mapper = ObjectMapperConfig.getInstance();

    private ReceiptTemplateRequest minimalReceipt() {
        return ReceiptTemplateRequest.builder()
                .documentNumber("INV-001")
                .issueDate(Instant.parse("2024-01-15T10:30:00Z"))
                .currency("EUR")
                .receiptSubtotal(new BigDecimal("100"))
                .totalBeforeTax(new BigDecimal("100"))
                .totalTaxAmount(new BigDecimal("21"))
                .totalAmount(new BigDecimal("121"))
                .addProduct(Product.builder()
                        .name("Item")
                        .quantity(1.0)
                        .unitCode(UnitCode.ONE)
                        .unitPrice("100")
                        .subtotal("100")
                        .total("121")
                        .addTax(21.0, "VAT", "100", "21")
                        .build())
                .addTax(21.0, "VAT", "100", "21")
                .build();
    }

    @Test
    void twoArgConstructorSerializesCorrectly() throws Exception {
        ReceiptTemplateGenerationRequest request = new ReceiptTemplateGenerationRequest(
                minimalReceipt(),
                List.of(ReceiptFormat.CHEQI)
        );

        JsonNode node = mapper.valueToTree(request);

        assertTrue(node.has("receiptTemplateRequest"));
        assertTrue(node.has("formats"));
        assertEquals(1, node.get("formats").size());
        // 2-arg constructor should not have VAT fields
        assertFalse(node.has("buyerCountryCode"));
        assertFalse(node.has("recipientEntityType"));
        assertFalse(node.has("taxesApplied"));
    }

    @Test
    void fiveArgConstructorHasVatFieldsAtTopLevel() throws Exception {
        ReceiptTemplateGenerationRequest request = new ReceiptTemplateGenerationRequest(
                minimalReceipt(),
                List.of(ReceiptFormat.CHEQI, ReceiptFormat.UBL_XML),
                "DE",
                RecipientEntityType.BUSINESS,
                true
        );

        JsonNode node = mapper.valueToTree(request);

        // VAT fields should be at the top level
        assertEquals("DE", node.get("buyerCountryCode").asText());
        assertEquals("BUSINESS", node.get("recipientEntityType").asText());
        assertTrue(node.get("taxesApplied").asBoolean());

        // They should NOT be nested inside receiptTemplateRequest
        JsonNode receiptNode = node.get("receiptTemplateRequest");
        assertNotNull(receiptNode);
        assertFalse(receiptNode.has("buyerCountryCode"),
                "buyerCountryCode must be at top level, not nested in receiptTemplateRequest");
        assertFalse(receiptNode.has("recipientEntityType"),
                "recipientEntityType must be at top level, not nested in receiptTemplateRequest");
        assertFalse(receiptNode.has("taxesApplied"),
                "taxesApplied must be at top level, not nested in receiptTemplateRequest");
    }
}
