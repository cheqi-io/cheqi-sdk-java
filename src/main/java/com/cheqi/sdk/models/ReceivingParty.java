package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceivingParty {
    @JsonProperty("companyLegalName")
    private String companyLegalName;
    
    @JsonProperty("taxNumber")
    private String taxNumber;
    
    @JsonProperty("companyRegistration")
    private CompanyRegistration companyRegistration;
    
    @JsonProperty("consumerName")
    private String consumerName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("address")
    private Address addressDto;

    public ReceivingParty() {
    }

    public String getCompanyLegalName() {
        return companyLegalName;
    }

    public void setCompanyLegalName(String companyLegalName) {
        this.companyLegalName = companyLegalName;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public CompanyRegistration getCompanyRegistration() {
        return companyRegistration;
    }

    public void setCompanyRegistration(CompanyRegistration companyRegistration) {
        this.companyRegistration = companyRegistration;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(Address addressDto) {
        this.addressDto = addressDto;
    }
}
