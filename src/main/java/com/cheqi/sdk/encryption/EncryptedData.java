package com.cheqi.sdk.encryption;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;

/**
 * Represents encrypted data with all necessary metadata for decryption.
 *
 * This class encapsulates:
 * - The encrypted content (Base64 encoded)
 * - Initialization vector (IV) for AES-GCM
 * - Encrypted symmetric key
 * - Algorithm information
 * - Key identifier for key management
 */
public class EncryptedData {

    private final byte[] ciphertext;
    private final byte[] iv;
    private final byte[] tag;

    public EncryptedData(byte[] ciphertext, byte[] iv, byte[] tag) {
        this.ciphertext = ciphertext;
        this.iv = iv;
        this.tag = tag;
    }

    public String toBase64String() {
        // Combine IV + ciphertext + tag for storage (iOS expects this format)
        byte[] combined = new byte[iv.length + ciphertext.length + tag.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
        System.arraycopy(tag, 0, combined, iv.length + ciphertext.length, tag.length);

        return Base64.getEncoder().encodeToString(combined);
    }
}