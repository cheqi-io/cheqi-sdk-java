package com.cheqi.sdk.models;

import com.cheqi.sdk.models.ubl.PaymentMeans;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = RecipientResolutionResponse.Builder.class)
public class RecipientResolutionResponse {
    @JsonProperty("customerFound")
    private boolean customerFound;

    @JsonProperty("matchId")
    private String matchId;

    @JsonProperty("recipients")
    private List<Recipient> recipients;

    @JsonProperty("paymentMeans")
    private List<PaymentMeans> paymentMeans;

    @JsonProperty("expiresAt")
    private Instant expiresAt;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("additionalProperties")
    private Map<String, Object> additionalProperties;

    // No-args constructor for Jackson deserialization
    public RecipientResolutionResponse() {
        this.additionalProperties = new HashMap<>();
    }

    // Private constructor for builder
    private RecipientResolutionResponse(
            boolean customerFound,
            String matchId,
            List<Recipient> recipients,
            List<PaymentMeans> paymentMeans,
            Instant expiresAt,
            String instructions,
            Map<String, Object> additionalProperties) {
        this.customerFound = customerFound;
        this.matchId = matchId;
        this.recipients = recipients;
        this.paymentMeans = paymentMeans;
        this.expiresAt = expiresAt;
        this.instructions = instructions != null ? instructions : "Use this anonymous identifier when submitting encrypted receipts. Encrypt receipts using the provided device public keys.";
        this.additionalProperties = additionalProperties != null ? additionalProperties : new HashMap<>();
    }

    // Getters
    public boolean isCustomerFound() {
        return customerFound;
    }

    public String getMatchId() {
        return matchId;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public List<PaymentMeans> getPaymentMeans() {
        return paymentMeans;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getInstructions() {
        return instructions;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    // Setters for Jackson deserialization
    public void setCustomerFound(boolean customerFound) {
        this.customerFound = customerFound;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public void setPaymentMeans(List<PaymentMeans> paymentMeans) {
        this.paymentMeans = paymentMeans;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof RecipientResolutionResponse && equalTo((RecipientResolutionResponse) other);
    }

    private boolean equalTo(RecipientResolutionResponse other) {
        return Objects.equals(this.customerFound, other.customerFound)
                && Objects.equals(this.matchId, other.matchId)
                && Objects.equals(this.recipients, other.recipients)
                && Objects.equals(this.expiresAt, other.expiresAt)
                && Objects.equals(this.instructions, other.instructions)
                && Objects.equals(this.additionalProperties, other.additionalProperties)
                && Objects.equals(this.paymentMeans, other.paymentMeans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.customerFound, this.matchId, this.recipients, this.expiresAt, this.instructions, this.paymentMeans, this.additionalProperties);
    }

    @Override
    public String toString() {
        return "RecipientResolutionResponse{" +
                "customerFound=" + customerFound +
                ", matchId='" + matchId + '\'' +
                ", recipients=" + recipients +
                ", expiresAt=" + expiresAt +
                ", instructions='" + instructions + '\'' +
                ", additionalProperties=" + additionalProperties +
                ", paymentMeans=" + paymentMeans +
                '}';
    }

    // Builder pattern for programmatic usage
    public static RecipientResolutionResponse.Builder builder() {
        return new RecipientResolutionResponse.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private boolean customerFound;
        private String matchId;
        private List<Recipient> recipients;
        private List<PaymentMeans> paymentMeans;
        private Instant expiresAt;
        private String instructions = "Use this anonymous identifier when submitting encrypted receipts. Encrypt receipts using the provided device public keys.";
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public RecipientResolutionResponse.Builder from(RecipientResolutionResponse other) {
            customerFound(other.isCustomerFound());
            matchId(other.getMatchId());
            recipients(other.getRecipients());
            expiresAt(other.getExpiresAt());
            paymentMeans(other.getPaymentMeans());
            instructions(other.getInstructions());
            return this;
        }

        @JsonSetter(value = "customerFound", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder customerFound(boolean customerFound) {
            this.customerFound = customerFound;
            return this;
        }

        @JsonSetter(value = "matchId", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder matchId(String matchId) {
            this.matchId = matchId;
            return this;
        }

        @JsonSetter(value = "recipients", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder recipients(List<Recipient> recipients) {
            this.recipients = recipients;
            return this;
        }

        @JsonSetter(value = "paymentMeans", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder paymentMeans(List<PaymentMeans> paymentMeans) {
            this.paymentMeans = paymentMeans;
            return this;
        }

        @JsonSetter(value = "expiresAt", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        @JsonSetter(value = "instructions", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        @JsonSetter(value = "additionalProperties", nulls = Nulls.SKIP)
        public RecipientResolutionResponse.Builder additionalProperties(Map<String, Object> additionalProperties) {
            this.additionalProperties = additionalProperties;
            return this;
        }

        public RecipientResolutionResponse build() {
            return new RecipientResolutionResponse(customerFound, matchId, recipients, paymentMeans, expiresAt, instructions, additionalProperties);
        }
    }

    // Helper factory methods
    public static RecipientResolutionResponse customerNotFound() {
        return RecipientResolutionResponse.builder()
                .customerFound(false)
                .instructions("No customer found with provided payment details")
                .build();
    }

    public static RecipientResolutionResponse customerNotFoundWithEmail(String email) {
        return RecipientResolutionResponse.builder()
                .customerFound(false)
                .instructions("No Cheqi registration found. Consider sending receipt via email to: " + email)
                .build();
    }

    public static RecipientResolutionResponse customerFound(List<Recipient> recipients) {
        return RecipientResolutionResponse.builder()
                .customerFound(true)
                .recipients(recipients)
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();
    }
}