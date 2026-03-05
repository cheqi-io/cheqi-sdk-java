package com.cheqi.sdk.models;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VatMetadataSerializationTest {

    private final ObjectMapper mapper = ObjectMapperConfig.getInstance();

    @Test
    void serializeDeserializeAllFields() throws Exception {
        VatMetadata original = new VatMetadata(
                "NL", "DE", true,
                RecipientEntityType.BUSINESS, true,
                VatRegime.REVERSE_CHARGE_EU, true
        );

        String json = mapper.writeValueAsString(original);
        VatMetadata deserialized = mapper.readValue(json, VatMetadata.class);

        assertEquals("NL", deserialized.getSupplierCountry());
        assertEquals("DE", deserialized.getBuyerCountry());
        assertTrue(deserialized.isCrossBorder());
        assertEquals(RecipientEntityType.BUSINESS, deserialized.getRecipientEntityType());
        assertTrue(deserialized.isVatOnReceipt());
        assertEquals(VatRegime.REVERSE_CHARGE_EU, deserialized.getVatRegime());
        assertTrue(deserialized.getTaxesApplied());
    }

    @Test
    void nullFieldsOmittedInJson() throws Exception {
        VatMetadata metadata = new VatMetadata();
        // Only set non-null fields
        metadata.setSupplierCountry("NL");

        JsonNode node = mapper.valueToTree(metadata);

        assertTrue(node.has("supplierCountry"));
        assertFalse(node.has("buyerCountry"), "Null buyerCountry should be omitted (@JsonInclude NON_NULL)");
        assertFalse(node.has("recipientEntityType"), "Null recipientEntityType should be omitted");
        assertFalse(node.has("vatRegime"), "Null vatRegime should be omitted");
        assertFalse(node.has("taxesApplied"), "Null taxesApplied should be omitted");
    }

    @Test
    void vatRegimeEnumSerializesCorrectly() throws Exception {
        for (VatRegime regime : VatRegime.values()) {
            VatMetadata metadata = new VatMetadata();
            metadata.setVatRegime(regime);

            String json = mapper.writeValueAsString(metadata);
            assertTrue(json.contains("\"" + regime.name() + "\""),
                    "VatRegime." + regime + " should serialize to its name");
        }
    }

    @Test
    void recipientEntityTypeEnumSerializesCorrectly() throws Exception {
        for (RecipientEntityType type : RecipientEntityType.values()) {
            VatMetadata metadata = new VatMetadata();
            metadata.setRecipientEntityType(type);

            String json = mapper.writeValueAsString(metadata);
            assertTrue(json.contains("\"" + type.name() + "\""),
                    "RecipientEntityType." + type + " should serialize to its name");
        }
    }
}
