package com.cheqi.sdk.receipt;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Exact issuer document references. Hashes are routing identifiers, not encryption. */
public final class DocumentReferences {
    private DocumentReferences() {}
    public static String hash(String documentNumber) {
        if (documentNumber == null || documentNumber.trim().isEmpty()) throw new IllegalArgumentException("documentNumber is required");
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(
                    ("cheqi:issuer-document:v1:" + documentNumber).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : digest) hex.append(String.format("%02x", b & 255));
            return hex.toString();
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    public static String receiptHash(String json) throws java.io.IOException {
        JsonNode node = ObjectMapperConfig.getInstance().readTree(json);
        return hash(node.path("documentNumber").asText(null));
    }
}
