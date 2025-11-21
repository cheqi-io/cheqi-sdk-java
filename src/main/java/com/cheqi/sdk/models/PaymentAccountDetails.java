package com.cheqi.sdk.models;

import com.cheqi.commons.enums.AccountIdentifierType;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

/**
 * Payment account details DTO representing payment account information.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>identifier</strong>: The account identifier (IBAN, account number, etc.)</li>
 *   <li><strong>type</strong>: The type of account identifier</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = PaymentAccountDetails.Builder.class)
public final class PaymentAccountDetails {

    // ===== MANDATORY FIELDS =====
    @JsonProperty("identifier")
    private String identifier;
    // IBAN, account number, wallet ID, etc.
    @JsonProperty("accountIdentifierType")
    private AccountIdentifierType type;
    @JsonProperty("bic")
    private String bic;                  // Optional - for IBAN payments
    @JsonProperty("bankName")
    private String bankName;             // Optional - for bank accounts
    @JsonProperty("accountHolderName")
    private String accountHolderName;    // Optional - for bank accounts
    @JsonProperty("institutionName")
    private String institutionName;      // Universal - bank, wallet provider, etc.

    // ===== CONSTRUCTOR =====

    private PaymentAccountDetails(
            String identifier,
            AccountIdentifierType type,
            String bic,
            String bankName,
            String accountHolderName,
            String institutionName) {
        this.identifier = identifier;
        this.type = type;
        this.bic = bic;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.institutionName = institutionName;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public String getIdentifier() {
        return identifier;
    }

    @JsonIgnore
    public AccountIdentifierType getType() {
        return type;
    }

    @JsonIgnore
    public String getBic() {
        return bic;
    }

    @JsonIgnore
    public String getBankName() {
        return bankName;
    }

    @JsonIgnore
    public String getAccountHolderName() {
        return accountHolderName;
    }

    @JsonIgnore
    public String getInstitutionName() {
        return institutionName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof PaymentAccountDetails && equalTo((PaymentAccountDetails) other);
    }

    private boolean equalTo(PaymentAccountDetails other) {
        return Objects.equals(this.identifier, other.identifier)
                && Objects.equals(this.bic, other.bic)
                && Objects.equals(this.bankName, other.bankName)
                && Objects.equals(this.accountHolderName, other.accountHolderName)
                && Objects.equals(this.institutionName, other.institutionName)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier, this.type);
    }

    @Override
    public String toString() {
        return "PaymentAccountDetails{" +
                "identifier='" + identifier + '\'' +
                ", type=" + type +
                ", bic='" + bic + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", institutionName='" + institutionName + '\'' +
                '}';
    }

    public static PaymentAccountDetails.Builder builder() {
        return new PaymentAccountDetails.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String identifier;
        private AccountIdentifierType type;
        private String bic;
        private String bankName;
        private String accountHolderName;
        private String institutionName;

        private Builder() {}

        public PaymentAccountDetails.Builder from(PaymentAccountDetails other) {
            identifier(other.getIdentifier());
            type(other.getType());
            return this;
        }

        @JsonSetter(value = "identifier", nulls = Nulls.SKIP)
        public PaymentAccountDetails.Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        @JsonSetter(value = "type", nulls = Nulls.SKIP)
        public PaymentAccountDetails.Builder type(AccountIdentifierType type) {
            this.type = type;
            return this;
        }

        @JsonSetter(value = "bic", nulls = Nulls.SKIP)
        public PaymentAccountDetails.Builder bic(String bic) {
            this.bic = bic;
            return this;
        }

        @JsonSetter(value = "bankName", nulls = Nulls.SKIP)
        public PaymentAccountDetails.Builder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        @JsonSetter(value = "accountHolderName", nulls = Nulls.SKIP)
        public PaymentAccountDetails.Builder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        @JsonSetter(value = "institutionName", nulls = Nulls.SKIP)
        public PaymentAccountDetails.Builder institutionName(String institutionName) {
            this.institutionName = institutionName;
            return this;
        }

        public PaymentAccountDetails build() {
            return new PaymentAccountDetails(identifier, type, bic, bankName, accountHolderName, institutionName);
        }
    }
}
