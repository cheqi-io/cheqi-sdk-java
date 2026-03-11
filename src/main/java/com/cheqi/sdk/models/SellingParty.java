package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SellingParty {
    @JsonProperty("companyName")
    private String companyName;
    
    @JsonProperty("companyLegalName")
    private String companyLegalName;
    
    @JsonProperty("taxNumber")
    private String taxNumber;
    
    @JsonProperty("identifiers")
    private List<CompanyIdentifier> identifiers;
    
    @JsonProperty("address")
    private Address addressDto;

    public SellingParty() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public List<CompanyIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<CompanyIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Address getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(Address addressDto) {
        this.addressDto = addressDto;
    }
}
