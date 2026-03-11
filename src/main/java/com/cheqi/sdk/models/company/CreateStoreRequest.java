package com.cheqi.sdk.models.company;

import com.cheqi.sdk.models.company.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Request object for creating a store/location for a company.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CreateStoreRequest.Builder.class)
public final class CreateStoreRequest {

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

    @JsonProperty("openingHours")
    private final String openingHours;

    private CreateStoreRequest(Builder builder) {
        this.storeName = builder.storeName;
        this.storeCode = builder.storeCode;
        this.address = builder.address;
        this.phoneNumber = builder.phoneNumber;
        this.email = builder.email;
        this.openingHours = builder.openingHours;
    }

    public String getStoreName() { return storeName; }
    public String getStoreCode() { return storeCode; }
    public Address getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getOpeningHours() { return openingHours; }

    public static Builder builder() { return new Builder(); }

    public boolean isValid() { return getValidationErrors().isEmpty(); }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        if (storeName == null || storeName.trim().isEmpty()) {
            errors.add("storeName is required");
        }
        if (address == null) {
            errors.add("address is required");
        } else if (!address.isValid()) {
            errors.addAll(address.getValidationErrors().stream()
                    .map(e -> "address." + e)
                    .collect(Collectors.toList()));
        }
        return errors;
    }

    public static final class Builder {
        private String storeName;
        private String storeCode;
        private Address address;
        private String phoneNumber;
        private String email;
        private String openingHours;

        public Builder storeName(String storeName) { this.storeName = storeName; return this; }
        public Builder storeCode(String storeCode) { this.storeCode = storeCode; return this; }
        public Builder address(Address address) { this.address = address; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder openingHours(String openingHours) { this.openingHours = openingHours; return this; }

        public CreateStoreRequest build() { return new CreateStoreRequest(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateStoreRequest)) return false;
        CreateStoreRequest that = (CreateStoreRequest) o;
        return Objects.equals(storeName, that.storeName) &&
                Objects.equals(storeCode, that.storeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, storeCode);
    }

    @Override
    public String toString() {
        return "CreateStoreRequest{" +
                "storeName='" + storeName + '\'' +
                ", storeCode='" + storeCode + '\'' +
                '}';
    }
}