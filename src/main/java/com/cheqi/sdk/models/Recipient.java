package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.*;

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
    private final UUID id;
    @JsonProperty("receiverType")
    private final ReceiverType receiverType;
    @JsonProperty("publicKey")
    private final String publicKey;
    @JsonProperty("keyAlgorithm")
    private final String keyAlgorithm;
    // ===== OPTIONAL FIELDS =====
    private final Optional<Instant> keyCreatedAt;
    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====
    private Recipient(
            UUID id,
            ReceiverType receiverType,
            String publicKey,
            String keyAlgorithm,
            Optional<Instant> keyCreatedAt,
            Map<String, Object> additionalProperties) {
        this.id = id;
        this.receiverType = receiverType;
        this.publicKey = publicKey;
        this.keyAlgorithm = keyAlgorithm;
        this.keyCreatedAt = keyCreatedAt;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public UUID getId() {
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

    // ===== OPTIONAL FIELD ACCESSORS =====

    @JsonIgnore
    public Optional<Instant> getKeyCreatedAt() {
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
                && Objects.equals(this.keyCreatedAt, other.keyCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.receiverType, this.publicKey, this.keyAlgorithm, this.keyCreatedAt);
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "id='" + id + '\'' +
                "receiverType='" + receiverType + '\'' +
                "publicKey='" + publicKey + '\'' +
                ", keyAlgorithm='" + keyAlgorithm + '\'' +
                ", keyCreatedAt=" + keyCreatedAt +
                '}';
    }

    public static Recipient.Builder builder() {
        return new Recipient.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private UUID id;
        private ReceiverType receiverType;
        private String publicKey;
        private String keyAlgorithm;
        private Optional<Instant> keyCreatedAt = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Recipient.Builder from(Recipient other) {
            id(other.getId());
            receiverType(other.getReceiverType());
            publicKey(other.getPublicKey());
            keyAlgorithm(other.getKeyAlgorithm());
            keyCreatedAt(other.getKeyCreatedAt());
            return this;
        }

        @JsonSetter(value="id", nulls = Nulls.SKIP)
        public Recipient.Builder id(UUID id) {
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

        @JsonSetter(value = "keyCreatedAt", nulls = Nulls.SKIP)
        public Recipient.Builder keyCreatedAt(Optional<Instant> keyCreatedAt) {
            this.keyCreatedAt = keyCreatedAt;
            return this;
        }

        public Recipient.Builder keyCreatedAt(Instant keyCreatedAt) {
            this.keyCreatedAt = Optional.ofNullable(keyCreatedAt);
            return this;
        }

        public Recipient build() {
            return new Recipient(id, receiverType, publicKey, keyAlgorithm, keyCreatedAt, additionalProperties);
        }
    }

    // ===== PRIVATE JSON SERIALIZATION METHODS =====

    @JsonProperty("keyCreatedAt")
    private Optional<Instant> _getKeyCreatedAt() {
        return keyCreatedAt;
    }
}
