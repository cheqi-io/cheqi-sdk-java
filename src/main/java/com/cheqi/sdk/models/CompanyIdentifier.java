package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyIdentifier {
    @JsonProperty("id")
    private String id;

    @JsonProperty("identifierType")
    private String identifierType;

    @JsonProperty("registrationScheme")
    private String registrationScheme;

    @JsonProperty("value")
    private String value;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("isVerified")
    private boolean isVerified;

    public CompanyIdentifier() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getRegistrationScheme() {
        return registrationScheme;
    }

    public void setRegistrationScheme(String registrationScheme) {
        this.registrationScheme = registrationScheme;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
