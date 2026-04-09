package com.cheqi.sdk.utils;

import java.util.Base64;

/**
 * Utility for handling PEM-encoded keys.
 * Strips PEM headers/footers and whitespace so both PEM-wrapped and raw base64 keys are accepted.
 */
public final class PemUtils {

    private PemUtils() {}

    /**
     * Strips PEM headers/footers and whitespace, returning raw base64 key bytes.
     * Accepts both PEM-wrapped keys (e.g. "-----BEGIN PUBLIC KEY-----...") and raw base64.
     */
    public static byte[] decodeKey(String keyString) {
        // If the input doesn't contain PEM headers, it may be base64-encoded PEM
        if (!keyString.contains("-----BEGIN")) {
            String decoded = new String(Base64.getDecoder().decode(keyString.replaceAll("\\s+", "")));
            if (decoded.contains("-----BEGIN")) {
                return decodeKey(decoded);
            }
        }

        String cleanKey = keyString
                .replaceAll("-+BEGIN [A-Z ]+KEY-+", "")
                .replaceAll("-+END [A-Z ]+KEY-+", "")
                .replaceAll("\\s+", "");
        return Base64.getDecoder().decode(cleanKey);
    }
}
