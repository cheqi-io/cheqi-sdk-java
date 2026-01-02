package com.cheqi.sdk.models.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Address.Builder.class)
public class Address {
    @JsonProperty("streetName")
    private final String streetName;

    @JsonProperty("additionalStreetName")
    private final String additionalStreetName;

    @JsonProperty("addressLine")
    private final String addressLine;

    @JsonProperty("region")
    private final String region;

    @JsonProperty("cityName")
    private final String cityName;

    @JsonProperty("postalZone")
    private final String postalZone;

    @JsonProperty("countryIsoCode")
    private final String countryIsoCode;

    private Address(Builder builder) {
        this.streetName = builder.streetName;
        this.additionalStreetName = builder.additionalStreetName;
        this.addressLine = builder.addressLine;
        this.region = builder.region;
        this.cityName = builder.cityName;
        this.postalZone = builder.postalZone;
        this.countryIsoCode = builder.countryIsoCode;
    }

    public String getStreetName() { return streetName; }
    public String getAdditionalStreetName() { return additionalStreetName; }
    public String getAddressLine() { return addressLine; }
    public String getRegion() { return region; }
    public String getCityName() { return cityName; }
    public String getPostalZone() { return postalZone; }
    public String getCountryIsoCode() { return countryIsoCode; }

    public static Builder builder() { return new Builder(); }

    public boolean isValid() { return getValidationErrors().isEmpty(); }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        if (streetName == null || streetName.trim().isEmpty()) {
            errors.add("streetName is required");
        }
        if (cityName == null || cityName.trim().isEmpty()) {
            errors.add("cityName is required");
        }
        if (postalZone == null || postalZone.trim().isEmpty()) {
            errors.add("postalZone is required");
        }
        if (countryIsoCode == null || countryIsoCode.trim().isEmpty()) {
            errors.add("countryIsoCode is required");
        } else if (countryIsoCode.length() != 2) {
            errors.add("countryIsoCode must be a 2-letter ISO code");
        }
        return errors;
    }

    public static final class Builder {
        private String streetName;
        private String additionalStreetName;
        private String addressLine;
        private String region;
        private String cityName;
        private String postalZone;
        private String countryIsoCode;

        public Builder streetName(String streetName) { this.streetName = streetName; return this; }
        public Builder additionalStreetName(String additionalStreetName) { this.additionalStreetName = additionalStreetName; return this; }
        public Builder addressLine(String addressLine) { this.addressLine = addressLine; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder cityName(String cityName) { this.cityName = cityName; return this; }
        public Builder postalZone(String postalZone) { this.postalZone = postalZone; return this; }
        public Builder countryIsoCode(String countryIsoCode) { this.countryIsoCode = countryIsoCode; return this; }

        public Address build() { return new Address(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address that = (Address) o;
        return Objects.equals(streetName, that.streetName) &&
                Objects.equals(additionalStreetName, that.additionalStreetName) &&
                Objects.equals(addressLine, that.addressLine) &&
                Objects.equals(region, that.region) &&
                Objects.equals(cityName, that.cityName) &&
                Objects.equals(postalZone, that.postalZone) &&
                Objects.equals(countryIsoCode, that.countryIsoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetName, additionalStreetName, addressLine, region, cityName, postalZone, countryIsoCode);
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetName='" + streetName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", postalZone='" + postalZone + '\'' +
                ", countryIsoCode='" + countryIsoCode + '\'' +
                '}';
    }
}
