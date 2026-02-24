package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Key information DTO representing cryptographic key details.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>publicKey</strong>: The public key string</li>
 *   <li><strong>keyAlgorithm</strong>: The algorithm used for the key</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>keyCreatedAt</strong>: When the key was created</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Recipient.Builder.class)
public class Recipient {
    // ===== MANDATORY FIELDS =====
    @JsonProperty("id")
    private final String id;
    @JsonProperty("receiverType")
    private final ReceiverType receiverType;
    @JsonProperty("publicKey")
    private final String publicKey;
    @JsonProperty("keyAlgorithm")
    private final String keyAlgorithm;
    @JsonProperty("acceptedFormats")
    private final List<ReceiptFormat> acceptedFormats;
    // ===== OPTIONAL FIELDS =====
    @JsonProperty("keyCreatedAt")
    private final Instant keyCreatedAt;
    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====
    private Recipient(
            String id,
            ReceiverType receiverType,
            String publicKey,
            String keyAlgorithm,
            List<ReceiptFormat> acceptedFormats,
            Instant keyCreatedAt,
            Map<String, Object> additionalProperties) {
        this.id = id;
        this.receiverType = receiverType;
        this.publicKey = publicKey;
        this.keyAlgorithm = keyAlgorithm;
        this.acceptedFormats = acceptedFormats;
        this.keyCreatedAt = keyCreatedAt;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public ReceiverType getReceiverType() {
        return receiverType;
    }

    @JsonIgnore
    public String getPublicKey() {
        return publicKey;
    }

    @JsonIgnore
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    @JsonIgnore
    public List<ReceiptFormat> getAcceptedFormats() {
        return acceptedFormats;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    public Instant getKeyCreatedAt() {
        return keyCreatedAt;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Recipient && equalTo((Recipient) other);
    }

    private boolean equalTo(Recipient other) {
        return   Objects.equals(this.id, other.id)
                && Objects.equals(this.receiverType, other.receiverType)
                && Objects.equals(this.publicKey, other.publicKey)
                && Objects.equals(this.keyAlgorithm, other.keyAlgorithm)
                && Objects.equals(this.acceptedFormats, other.acceptedFormats)
                && Objects.equals(this.keyCreatedAt, other.keyCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.receiverType, this.publicKey, this.keyAlgorithm, this.acceptedFormats, this.keyCreatedAt);
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "id='" + id + '\'' +
                "receiverType='" + receiverType + '\'' +
                "publicKey='" + publicKey + '\'' +
                ", keyAlgorithm='" + keyAlgorithm + '\'' +
                ", acceptedFormats=" + acceptedFormats +
                ", keyCreatedAt=" + keyCreatedAt +
                '}';
    }

    public static Recipient.Builder builder() {
        return new Recipient.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private ReceiverType receiverType;
        private String publicKey;
        private String keyAlgorithm;
        private List<ReceiptFormat> acceptedFormats;
        private Instant keyCreatedAt;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Recipient.Builder from(Recipient other) {
            id(other.getId());
            receiverType(other.getReceiverType());
            publicKey(other.getPublicKey());
            keyAlgorithm(other.getKeyAlgorithm());
            keyCreatedAt(other.getKeyCreatedAt());
            acceptedFormats(other.getAcceptedFormats());
            return this;
        }

        @JsonSetter(value="id", nulls = Nulls.SKIP)
        public Recipient.Builder id(String id) {
            this.id = id;
            return this;
        }

        @JsonSetter(value = "receiverType", nulls = Nulls.SKIP)
        public Recipient.Builder receiverType(ReceiverType receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        @JsonSetter(value = "publicKey", nulls = Nulls.SKIP)
        public Recipient.Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        @JsonSetter(value = "keyAlgorithm", nulls = Nulls.SKIP)
        public Recipient.Builder keyAlgorithm(String keyAlgorithm) {
            this.keyAlgorithm = keyAlgorithm;
            return this;
        }

        @JsonSetter(value = "acceptedFormats", nulls = Nulls.SKIP)
        public Recipient.Builder acceptedFormats(List<ReceiptFormat> acceptedFormats) {
            this.acceptedFormats = acceptedFormats;
            return this;
        }

        @JsonSetter(value = "keyCreatedAt", nulls = Nulls.SKIP)
        public Recipient.Builder keyCreatedAt(Instant keyCreatedAt) {
            this.keyCreatedAt = keyCreatedAt;
            return this;
        }

        public Recipient build() {
            return new Recipient(id, receiverType, publicKey, keyAlgorithm, acceptedFormats, keyCreatedAt, additionalProperties);
        }
    }

}
