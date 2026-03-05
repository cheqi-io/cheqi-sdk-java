package com.cheqi.sdk.models;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptTemplateRequestSerializationTest {

    private final ObjectMapper mapper = ObjectMapperConfig.getInstance();

    private ReceiptTemplateRequest.Builder minimalBuilder() {
        return ReceiptTemplateRequest.builder()
                .documentNumber("INV-001")
                .issueDate(Instant.parse("2024-01-15T10:30:00Z"))
                .currency("EUR")
                .receiptSubtotal(new BigDecimal("100.00"))
                .totalBeforeTax(new BigDecimal("100.00"))
                .totalTaxAmount(new BigDecimal("21.00"))
                .totalAmount(new BigDecimal("121.00"))
                .addProduct(Product.builder()
                        .name("Widget")
                        .quantity(1.0)
                        .unitCode(UnitCode.ONE)
                        .unitPrice("100.00")
                        .subtotal("100.00")
                        .total("121.00")
                        .addTax(21.0, "VAT", "100.00", "21.00")
                        .build())
                .addTax(21.0, "VAT", "100.00", "21.00");
    }

    @Test
    void builderToJsonRoundTrip() throws Exception {
        ReceiptTemplateRequest receipt = minimalBuilder().build();

        String json = mapper.writeValueAsString(receipt);
        ReceiptTemplateRequest deserialized = mapper.readValue(json, ReceiptTemplateRequest.class);

        // BigDecimal trailing zeros are stripped during serialization (100.00 → 100),
        // so we compare key fields individually using compareTo for BigDecimal
        assertEquals(receipt.getDocumentNumber(), deserialized.getDocumentNumber());
        assertEquals(receipt.getcurrency(), deserialized.getcurrency());
        assertEquals(0, receipt.getTotalAmount().compareTo(deserialized.getTotalAmount()));
        assertEquals(0, receipt.getreceiptSubtotal().compareTo(deserialized.getreceiptSubtotal()));
        assertEquals(receipt.getProducts().size(), deserialized.getProducts().size());
        assertEquals(receipt.getTaxes().size(), deserialized.getTaxes().size());
    }

    @Test
    void mandatoryFieldsPresentInJson() throws Exception {
        ReceiptTemplateRequest receipt = minimalBuilder().build();
        JsonNode node = mapper.valueToTree(receipt);

        assertTrue(node.has("documentNumber"));
        assertTrue(node.has("issueDate"));
        assertTrue(node.has("currency"));
        assertTrue(node.has("receiptSubtotal"));
        assertTrue(node.has("totalBeforeTax"));
        assertTrue(node.has("totalTaxAmount"));
        assertTrue(node.has("totalAmount"));
        assertTrue(node.has("products"));
        assertTrue(node.has("taxes"));
    }

    @Test
    void optionalFieldsOmittedWhenEmpty() throws Exception {
        ReceiptTemplateRequest receipt = minimalBuilder().build();
        JsonNode node = mapper.valueToTree(receipt);

        assertFalse(node.has("transactionDate"));
        assertFalse(node.has("purchaseDate"));
        assertFalse(node.has("period"));
        assertFalse(node.has("note"));
        assertFalse(node.has("childCompanyId"));
    }

    @Test
    void vatMetadataFieldsNotInSerializedJson() throws Exception {
        ReceiptTemplateRequest receipt = minimalBuilder()
                .buyerCountryCode("DE")
                .recipientEntityType(RecipientEntityType.BUSINESS)
                .taxesApplied(true)
                .build();
        JsonNode node = mapper.valueToTree(receipt);

        assertFalse(node.has("buyerCountryCode"), "buyerCountryCode should be @JsonIgnore");
        assertFalse(node.has("recipientEntityType"), "recipientEntityType should be @JsonIgnore");
        assertFalse(node.has("taxesApplied"), "taxesApplied should be @JsonIgnore");

        // But they should be accessible via getters
        assertEquals("DE", receipt.getBuyerCountryCode());
        assertEquals(RecipientEntityType.BUSINESS, receipt.getRecipientEntityType());
        assertTrue(receipt.getTaxesApplied());
    }

    @Test
    void validCurrencyAccepted() {
        assertDoesNotThrow(() -> minimalBuilder().currency("USD").build());
        assertDoesNotThrow(() -> minimalBuilder().currency("GBP").build());
        assertDoesNotThrow(() -> minimalBuilder().currency("JPY").build());
    }

    @Test
    void invalidCurrencyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ReceiptTemplateRequest.builder().currency("INVALID"));
        assertThrows(IllegalArgumentException.class,
                () -> ReceiptTemplateRequest.builder().currency("XYZ"));
    }

    @Test
    void addProductConvenienceMethod() throws Exception {
        ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
                .documentNumber("INV-002")
                .issueDate(Instant.now())
                .currency("EUR")
                .receiptSubtotal(new BigDecimal("250.00"))
                .totalBeforeTax("245.00")
                .totalTaxAmount("51.45")
                .totalAmount("296.45")
                .addProduct("SKU-001", "Nike", "AirMax", "125.00", "250.00",
                        21.0, "250.00", "52.50", "302.50", 2.0)
                .addTax(21.0, "VAT", "245.00", "51.45")
                .build();

        assertEquals(1, receipt.getProducts().size());
        assertEquals("AirMax", receipt.getProducts().get(0).getName());
    }

    @Test
    void addDiscountConvenienceMethod() {
        ReceiptTemplateRequest receipt = minimalBuilder()
                .addDiscount("5.00", "Loyalty")
                .addDiscount(new BigDecimal("3.00"), 10.0, "Member discount")
                .build();

        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    void addChargeConvenienceMethod() {
        ReceiptTemplateRequest receipt = minimalBuilder()
                .addCharge("3.50", "Service fee")
                .build();

        assertEquals(1, receipt.getCharges().size());
    }

    @Test
    void addTaxConvenienceMethod() {
        ReceiptTemplateRequest receipt = minimalBuilder()
                .addTax(9.0, "VAT", "50.00", "4.50")
                .build();

        // 1 from minimalBuilder + 1 added here
        assertEquals(2, receipt.getTaxes().size());
    }
}
