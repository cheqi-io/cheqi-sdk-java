package com.cheqi.sdk.download;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.decryption.AESDecryptor;
import com.cheqi.sdk.encryption.AESEncryptor;
import com.cheqi.sdk.encryption.EncryptedData;
import com.cheqi.sdk.encryption.EncryptionException;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.ReceiptTemplateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Stateless helpers for client-generated, end-to-end encrypted receipt download links.
 * Storage, scheduling, retries, and retention belong to the merchant integration.
 */
public class DownloadService {
    public static final int DOWNLOAD_ID_LENGTH = 16;
    public static final int CONTENT_KEY_LENGTH = 32;
    public static final String PRODUCTION_BASE_URL = "https://receipt.cheqi.io";
    public static final String SANDBOX_BASE_URL = "https://sandbox.receipt.cheqi.io";

    private final SecureRandom secureRandom;
    private final ObjectMapper objectMapper;

    public DownloadService() {
        this(new SecureRandom(), ObjectMapperConfig.getInstance());
    }

    public DownloadService(SecureRandom secureRandom, ObjectMapper objectMapper) {
        this.secureRandom = Objects.requireNonNull(secureRandom, "secureRandom");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    /** Generates a URL for the customer-facing origin corresponding to the API environment. */
    public DownloadLink generateDownloadLink(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("environment cannot be null");
        }
        return generateDownloadLink(environment == Environment.PRODUCTION
                ? PRODUCTION_BASE_URL
                : SANDBOX_BASE_URL);
    }

    /** Generates a URL locally for a custom origin such as a localhost frontend. */
    public DownloadLink generateDownloadLink(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("baseUrl cannot be null or empty");
        }
        byte[] id = randomBytes(DOWNLOAD_ID_LENGTH);
        byte[] key = randomBytes(CONTENT_KEY_LENGTH);
        String downloadId = Base64.getUrlEncoder().withoutPadding().encodeToString(id);
        String contentKey = Base64.getUrlEncoder().withoutPadding().encodeToString(key);
        return new DownloadLink(downloadId, contentKey, buildDownloadUrl(baseUrl, downloadId, contentKey));
    }

    public String buildDownloadUrl(String baseUrl, String downloadId, String contentKey) {
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalizedBase + "/" + downloadId + "#" + contentKey;
    }

    /** Parses an existing URL so an integrator can resume deferred processing. */
    public DownloadLink parseDownloadUrl(String url) {
        try {
            URI uri = URI.create(url);
            String fragment = uri.getRawFragment();
            String path = uri.getPath();
            int slash = path == null ? -1 : path.lastIndexOf('/');
            String downloadId = slash >= 0 ? path.substring(slash + 1) : path;
            validateDownloadId(downloadId);
            decodeContentKey(fragment);
            return new DownloadLink(downloadId, fragment, url);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid receipt download URL", e);
        }
    }

    /** Maps the canonical server template response to the standard encrypted envelope. */
    public ReceiptEnvelope buildDownloadEnvelope(ReceiptTemplateResponse template) {
        if (template == null) {
            throw new IllegalArgumentException("template cannot be null");
        }
        return new ReceiptEnvelope()
                .cheqi(template.getCheqi())
                .ublPurchaseReceipt(template.getUblPurchaseReceipt())
                .ublInvoice(template.getUblInvoice())
                .vatMetaData(template.getVatMetadata());
    }

    /** Returns Base64(iv || ciphertext || tag), compatible with the JavaScript SDK. */
    public String encryptDownloadEnvelope(ReceiptEnvelope envelope, String contentKey) {
        if (envelope == null) {
            throw new IllegalArgumentException("envelope cannot be null");
        }
        try {
            String plaintext = objectMapper.writeValueAsString(envelope);
            EncryptedData encrypted = new AESEncryptor(secureRandom).encrypt(plaintext, decodeContentKey(contentKey));
            return encrypted.toBase64String();
        } catch (EncryptionException e) {
            throw e;
        } catch (Exception e) {
            throw new EncryptionException("Download envelope encryption failed", e);
        }
    }

    public ReceiptEnvelope decryptDownloadEnvelope(String ciphertext, String contentKey) {
        try {
            String plaintext = new AESDecryptor().decrypt(ciphertext, decodeContentKey(contentKey));
            return objectMapper.readValue(plaintext, ReceiptEnvelope.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Download envelope decryption failed", e);
        }
    }

    private byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    private SecretKey decodeContentKey(String contentKey) {
        try {
            byte[] key = Base64.getUrlDecoder().decode(contentKey);
            if (key.length != CONTENT_KEY_LENGTH) {
                throw new IllegalArgumentException("contentKey must decode to 32 bytes");
            }
            return new SecretKeySpec(key, "AES");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("contentKey must be an unpadded base64url AES-256 key", e);
        }
    }

    private void validateDownloadId(String downloadId) {
        if (downloadId == null || !downloadId.matches("[A-Za-z0-9_-]{22,64}")) {
            throw new IllegalArgumentException("downloadId must be 22-64 base64url characters");
        }
    }
}
