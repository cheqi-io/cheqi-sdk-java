package com.cheqi.sdk.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class XMLCanonicalServiceTest {

    private final XMLCanonicalService service = new XMLCanonicalService();

    @Test
    void calculateHash_ignoresFormattingWhitespaceBetweenElements() {
        String compactXml = "<Invoice><AccountingCustomerParty><Party><Name>Cheqi</Name></Party></AccountingCustomerParty></Invoice>";
        String formattedXml = "<Invoice>\n"
                + "  <AccountingCustomerParty>\n"
                + "    <Party>\n"
                + "      <Name>Cheqi</Name>\n"
                + "    </Party>\n"
                + "  </AccountingCustomerParty>\n"
                + "</Invoice>";

        assertEquals(service.calculateHash(compactXml), service.calculateHash(formattedXml));
    }

    @Test
    void calculateHash_preservesMeaningfulTextWhitespace() {
        String singleSpaced = "<Invoice><Name>Cheqi BV</Name></Invoice>";
        String doubleSpaced = "<Invoice><Name>Cheqi  BV</Name></Invoice>";

        assertNotEquals(service.calculateHash(singleSpaced), service.calculateHash(doubleSpaced));
    }
}
