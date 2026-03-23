package com.cheqi.sdk.models;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.BuyerType;
import com.cheqi.sdk.models.generated.VatMetadata;
import com.cheqi.sdk.models.generated.VatRegime;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VatMetadataSerializationTest {

    private final ObjectMapper mapper = ObjectMapperConfig.getInstance();

    @Test
    void serializeDeserializeAllFields() throws Exception {
        VatMetadata original = new VatMetadata()
                .supplierCountry("NL")
                .buyerCountry("DE")
                .crossBorder(true)
                .buyerType(BuyerType.BUSINESS)
                .vatOnReceipt(true)
                .vatRegime(VatRegime.REVERSE_CHARGE_EU)
                .taxesApplied(true);

        String json = mapper.writeValueAsString(original);
        VatMetadata deserialized = mapper.readValue(json, VatMetadata.class);

        assertEquals("NL", deserialized.getSupplierCountry());
        assertEquals("DE", deserialized.getBuyerCountry());
        assertTrue(deserialized.getCrossBorder());
        assertEquals(BuyerType.BUSINESS, deserialized.getBuyerType());
        assertTrue(deserialized.getVatOnReceipt());
        assertEquals(VatRegime.REVERSE_CHARGE_EU, deserialized.getVatRegime());
        assertTrue(deserialized.getTaxesApplied());
    }

    @Test
    void nullFieldsOmittedInJson() throws Exception {
        VatMetadata metadata = new VatMetadata();
        metadata.setSupplierCountry("NL");

        JsonNode node = mapper.valueToTree(metadata);

        assertTrue(node.has("supplierCountry"));
        assertFalse(node.has("buyerCountry"), "Null buyerCountry should be omitted (@JsonInclude NON_NULL)");
        assertFalse(node.has("buyerType"), "Null buyerType should be omitted");
        assertFalse(node.has("vatRegime"), "Null vatRegime should be omitted");
        assertFalse(node.has("taxesApplied"), "Null taxesApplied should be omitted");
    }

    @Test
    void vatRegimeEnumSerializesCorrectly() throws Exception {
        for (VatRegime regime : VatRegime.values()) {
            VatMetadata metadata = new VatMetadata();
            metadata.setVatRegime(regime);

            String json = mapper.writeValueAsString(metadata);
            assertTrue(json.contains("\"" + regime.getValue() + "\""),
                    "VatRegime." + regime + " should serialize to its name");
        }
    }

    @Test
    void buyerTypeEnumSerializesCorrectly() throws Exception {
        for (BuyerType type : BuyerType.values()) {
            VatMetadata metadata = new VatMetadata();
            metadata.setBuyerType(type);

            String json = mapper.writeValueAsString(metadata);
            assertTrue(json.contains("\"" + type.getValue() + "\""),
                    "BuyerType." + type + " should serialize to its name");
        }
    }
}
