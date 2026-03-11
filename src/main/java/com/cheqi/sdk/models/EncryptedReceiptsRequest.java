package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = EncryptedReceiptsRequest.Builder.class)
public final class EncryptedReceiptsRequest {
    @JsonProperty("encryptedReceipts")
    private final Set<EncryptedReceiptRequestDto> encryptedReceipts;

    @JsonProperty("matchId")
    private final String matchId;

    @JsonProperty("templateHash")
    private final String templateHash;

    private EncryptedReceiptsRequest(
            String matchId,
            Set<EncryptedReceiptRequestDto> encryptedReceipts,
            String templateHash) {
        this.encryptedReceipts = encryptedReceipts;
        this.matchId = matchId;
        this.templateHash = templateHash;
    }

    public Set<EncryptedReceiptRequestDto> getEncryptedReceipts() {
        return encryptedReceipts;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getTemplateHash() {
        return templateHash;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof EncryptedReceiptsRequest && equalTo((EncryptedReceiptsRequest) other);
    }

    private boolean equalTo(EncryptedReceiptsRequest other) {
        return Objects.equals(this.encryptedReceipts, other.encryptedReceipts)
                && Objects.equals(this.matchId, other.matchId)
                && Objects.equals(this.templateHash, other.templateHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.encryptedReceipts, this.matchId, this.templateHash);
    }

    @Override
    public String toString() {
        return "EncryptedReceiptsRequest{" +
                "encryptedReceipts=" + encryptedReceipts +
                ", matchId='" + matchId + '\'' +
                ", templateHash='" + templateHash + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Set<EncryptedReceiptRequestDto> encryptedReceipts;
        private String matchId;
        private String templateHash;

        private Builder() {}

        public Builder from(EncryptedReceiptsRequest other) {
            encryptedReceipts(other.getEncryptedReceipts());
            matchId(other.getMatchId());
            templateHash(other.getTemplateHash());
            return this;
        }

        @JsonSetter(value = "encryptedReceipts", nulls = Nulls.SKIP)
        public Builder encryptedReceipts(Set<EncryptedReceiptRequestDto> encryptedReceipts) {
            this.encryptedReceipts = encryptedReceipts;
            return this;
        }

        @JsonSetter(value = "matchId", nulls = Nulls.SKIP)
        public Builder matchId(String matchId) {
            this.matchId = matchId;
            return this;
        }

        @JsonSetter(value = "templateHash", nulls = Nulls.SKIP)
        public Builder templateHash(String templateHash) {
            this.templateHash = templateHash;
            return this;
        }

        public EncryptedReceiptsRequest build() {
            return new EncryptedReceiptsRequest(matchId, encryptedReceipts, templateHash);
        }
    }
}