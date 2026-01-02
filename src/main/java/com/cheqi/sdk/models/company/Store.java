package com.cheqi.sdk.models.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.UUID;

/**
 * Store data transfer object representing a store/location.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = Store.Builder.class)
public final class Store {

    @JsonProperty("id")
    private final UUID id;

    @JsonProperty("companyId")
    private final UUID companyId;

    @JsonProperty("storeName")
    private final String storeName;

    @JsonProperty("storeCode")
    private final String storeCode;

    @JsonProperty("address")
    private final Address address;

    @JsonProperty("phoneNumber")
    private final String phoneNumber;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("isActive")
    private final Boolean isActive;

    @JsonProperty("openingHours")
    private final String openingHours;

    @JsonProperty("createdAt")
    private final Instant createdAt;

    @JsonProperty("updatedAt")
    private final Instant updatedAt;

    private Store(Builder builder) {
        this.id = builder.id;
        this.companyId = builder.companyId;
        this.storeName = builder.storeName;
        this.storeCode = builder.storeCode;
        this.address = builder.address;
        this.phoneNumber = builder.phoneNumber;
        this.email = builder.email;
        this.isActive = builder.isActive;
        this.openingHours = builder.openingHours;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public String getStoreName() { return storeName; }
    public String getStoreCode() { return storeCode; }
    public Address getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public Boolean getIsActive() { return isActive; }
    public String getOpeningHours() { return openingHours; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID id;
        private UUID companyId;
        private String storeName;
        private String storeCode;
        private Address address;
        private String phoneNumber;
        private String email;
        private Boolean isActive;
        private String openingHours;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder companyId(UUID companyId) { this.companyId = companyId; return this; }
        public Builder storeName(String storeName) { this.storeName = storeName; return this; }
        public Builder storeCode(String storeCode) { this.storeCode = storeCode; return this; }
        public Builder address(Address address) { this.address = address; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder openingHours(String openingHours) { this.openingHours = openingHours; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public Store build() { return new Store(this); }
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", storeName='" + storeName + '\'' +
                ", storeCode='" + storeCode + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}