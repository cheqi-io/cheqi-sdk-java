package com.cheqi.sdk.models.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Company data transfer object for company provisioning.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Company.Builder.class)
public class Company {
    @JsonProperty("companyName")
    private final String companyName;

    @JsonProperty("companyLegalName")
    private final String companyLegalName;

    @JsonProperty("chamberOfCommerceNumber")
    private final String chamberOfCommerceNumber;

    @JsonProperty("taxNumber")
    private final String taxNumber;

    @JsonProperty("companyEmail")
    private final String companyEmail;

    @JsonProperty("phoneNumber")
    private final String phoneNumber;

    @JsonProperty("website")
    private final String website;

    @JsonProperty("returnDays")
    private final Integer returnDays;

    @JsonProperty("companyReceiptText")
    private final String companyReceiptText;

    @JsonProperty("companyLogoUrl")
    private final String companyLogoUrl;

    @JsonProperty("address")
    private final Address address;

    private Company(Builder builder) {
        this.companyName = builder.companyName;
        this.companyLegalName = builder.companyLegalName;
        this.chamberOfCommerceNumber = builder.chamberOfCommerceNumber;
        this.taxNumber = builder.taxNumber;
        this.companyEmail = builder.companyEmail;
        this.phoneNumber = builder.phoneNumber;
        this.website = builder.website;
        this.returnDays = builder.returnDays;
        this.companyReceiptText = builder.companyReceiptText;
        this.companyLogoUrl = builder.companyLogoUrl;
        this.address = builder.address;
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyLegalName() { return companyLegalName; }
    public String getChamberOfCommerceNumber() { return chamberOfCommerceNumber; }
    public String getTaxNumber() { return taxNumber; }
    public String getCompanyEmail() { return companyEmail; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWebsite() { return website; }
    public Integer getReturnDays() { return returnDays; }
    public String getCompanyReceiptText() { return companyReceiptText; }
    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public Address getAddress() { return address; }

    public static Builder builder() { return new Builder(); }

    public boolean isValid() { return getValidationErrors().isEmpty(); }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        if (companyName == null || companyName.trim().isEmpty()) {
            errors.add("companyName is required");
        }
        if (companyEmail == null || companyEmail.trim().isEmpty()) {
            errors.add("companyEmail is required");
        } else if (!companyEmail.contains("@")) {
            errors.add("companyEmail must be a valid email address");
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
        private String companyName;
        private String companyLegalName;
        private String chamberOfCommerceNumber;
        private String taxNumber;
        private String companyEmail;
        private String phoneNumber;
        private String website;
        private Integer returnDays;
        private String companyReceiptText;
        private String companyLogoUrl;
        private Address address;

        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder companyLegalName(String companyLegalName) { this.companyLegalName = companyLegalName; return this; }
        public Builder chamberOfCommerceNumber(String chamberOfCommerceNumber) { this.chamberOfCommerceNumber = chamberOfCommerceNumber; return this; }
        public Builder taxNumber(String taxNumber) { this.taxNumber = taxNumber; return this; }
        public Builder companyEmail(String companyEmail) { this.companyEmail = companyEmail; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder website(String website) { this.website = website; return this; }
        public Builder returnDays(Integer returnDays) { this.returnDays = returnDays; return this; }
        public Builder companyReceiptText(String companyReceiptText) { this.companyReceiptText = companyReceiptText; return this; }
        public Builder companyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; return this; }
        public Builder address(Address address) { this.address = address; return this; }

        public Company build() { return new Company(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        Company that = (Company) o;
        return Objects.equals(companyName, that.companyName) &&
                Objects.equals(chamberOfCommerceNumber, that.chamberOfCommerceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, chamberOfCommerceNumber);
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyName='" + companyName + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", chamberOfCommerceNumber='" + chamberOfCommerceNumber + '\'' +
                '}';
    }
}
