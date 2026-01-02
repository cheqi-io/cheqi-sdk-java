package com.cheqi.sdk.models.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Request object for provisioning a new company.
 * Used by trusted POS systems to create companies and receive immediate API access.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ProvisionCompanyRequest.Builder.class)
public final class ProvisionCompanyRequest {

    @JsonProperty("company")
    private final Company company;

    @JsonProperty("adminEmail")
    private final String adminEmail;

    private ProvisionCompanyRequest(Builder builder) {
        this.company = builder.company;
        this.adminEmail = builder.adminEmail;
    }

    public Company getCompany() { return company; }
    public String getAdminEmail() { return adminEmail; }

    public static Builder builder() { return new Builder(); }

    public boolean isValid() { return getValidationErrors().isEmpty(); }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        if (company == null) {
            errors.add("company is required");
        } else if (!company.isValid()) {
            errors.addAll(company.getValidationErrors().stream()
                    .map(e -> "company." + e)
                    .collect(Collectors.toList()));
        }
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            errors.add("adminEmail is required");
        } else if (!adminEmail.contains("@")) {
            errors.add("adminEmail must be a valid email address");
        }
        return errors;
    }

    public static final class Builder {
        private Company company;
        private String adminEmail;

        public Builder company(Company company) { this.company = company; return this; }
        public Builder adminEmail(String adminEmail) { this.adminEmail = adminEmail; return this; }

        public ProvisionCompanyRequest build() { return new ProvisionCompanyRequest(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvisionCompanyRequest)) return false;
        ProvisionCompanyRequest that = (ProvisionCompanyRequest) o;
        return Objects.equals(company, that.company) &&
                Objects.equals(adminEmail, that.adminEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, adminEmail);
    }

    @Override
    public String toString() {
        return "ProvisionCompanyRequest{" +
                "company=" + company +
                ", adminEmail='" + adminEmail + '\'' +
                '}';
    }
}