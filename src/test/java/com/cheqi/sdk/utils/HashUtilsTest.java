package com.cheqi.sdk.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilsTest {

    @Test
    void knownSha256VectorMatchesExpected() {
        // SHA-256("hello") — well-known test vector
        String expected = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
        assertEquals(expected, HashUtils.sha256Hex("hello"));
    }

    @Test
    void nullStringThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HashUtils.sha256Hex((String) null));
    }

    @Test
    void nullBytesThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HashUtils.sha256Hex((byte[]) null));
    }

    @Test
    void outputIsDeterministic() {
        String input = "test-receipt-data-12345";
        String hash1 = HashUtils.sha256Hex(input);
        String hash2 = HashUtils.sha256Hex(input);
        assertEquals(hash1, hash2);
    }

    @Test
    void outputIs64CharLowercaseHex() {
        String hash = HashUtils.sha256Hex("anything");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"), "Must be lowercase hex");
    }

    @Test
    void emptyStringHashesCorrectly() {
        // SHA-256("") — known vector
        String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        assertEquals(expected, HashUtils.sha256Hex(""));
    }
}
