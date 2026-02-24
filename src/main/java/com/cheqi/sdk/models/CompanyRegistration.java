package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyRegistration {
    @JsonProperty("value")
    private String value;
    
    @JsonProperty("type")
    private CompanyRegistrationScheme type;

    public CompanyRegistration() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CompanyRegistrationScheme getType() {
        return type;
    }

    public void setType(CompanyRegistrationScheme type) {
        this.type = type;
    }
}
