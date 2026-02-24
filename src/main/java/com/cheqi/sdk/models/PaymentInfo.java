package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PaymentInfo.Builder.class)
public class PaymentInfo {
    @JsonProperty("paymentType")
    private final PaymentType paymentType;

    // Card payment fields
    @JsonProperty("par")
    private final String par;

    @JsonProperty("cardProvider")
    private final String cardProvider;

    @JsonProperty("lastFourDigits")
    private final String lastFourDigits;

    // Bank/IBAN payment fields
    @JsonProperty("iban")
    private final String iban;

    @JsonProperty("bic")
    private final String bic;

    @JsonProperty("bankName")
    private final String bankName;

    @JsonProperty("accountHolderName")
    private final String accountHolderName;

    // General fields
    @JsonProperty("paymentIds")
    private final List<String> paymentIds;

    private PaymentInfo(
            PaymentType paymentType,
            String par,
            String cardProvider,
            String lastFourDigits,
            String iban,
            String bic,
            String bankName,
            String accountHolderName,
            List<String> paymentIds) {
        this.paymentType = paymentType;
        this.par = par;
        this.cardProvider = cardProvider;
        this.lastFourDigits = lastFourDigits;
        this.iban = iban;
        this.bic = bic;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.paymentIds = paymentIds;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getPar() {
        return par;
    }

    public String getCardProvider() {
        return cardProvider;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public List<String> getPaymentIds() {
        return paymentIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentInfo)) return false;
        PaymentInfo that = (PaymentInfo) o;
        return paymentType == that.paymentType
                && Objects.equals(par, that.par)
                && Objects.equals(cardProvider, that.cardProvider)
                && Objects.equals(lastFourDigits, that.lastFourDigits)
                && Objects.equals(iban, that.iban)
                && Objects.equals(bic, that.bic)
                && Objects.equals(bankName, that.bankName)
                && Objects.equals(accountHolderName, that.accountHolderName)
                && Objects.equals(paymentIds, that.paymentIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentType, par, cardProvider, lastFourDigits, iban, bic, bankName, accountHolderName, paymentIds);
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "paymentType=" + paymentType +
                ", par='" + par + '\'' +
                ", cardProvider='" + cardProvider + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", paymentIds=" + paymentIds +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private PaymentType paymentType;
        private String par;
        private String cardProvider;
        private String lastFourDigits;
        private String iban;
        private String bic;
        private String bankName;
        private String accountHolderName;
        private List<String> paymentIds;

        private Builder() {}

        @JsonSetter(value = "paymentType", nulls = Nulls.SKIP)
        public Builder paymentType(PaymentType paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        @JsonSetter(value = "par", nulls = Nulls.SKIP)
        public Builder par(String par) {
            this.par = par;
            return this;
        }

        @JsonSetter(value = "cardProvider", nulls = Nulls.SKIP)
        public Builder cardProvider(String cardProvider) {
            this.cardProvider = cardProvider;
            return this;
        }

        @JsonSetter(value = "lastFourDigits", nulls = Nulls.SKIP)
        public Builder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }

        @JsonSetter(value = "iban", nulls = Nulls.SKIP)
        public Builder iban(String iban) {
            this.iban = iban;
            return this;
        }

        @JsonSetter(value = "bic", nulls = Nulls.SKIP)
        public Builder bic(String bic) {
            this.bic = bic;
            return this;
        }

        @JsonSetter(value = "bankName", nulls = Nulls.SKIP)
        public Builder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        @JsonSetter(value = "accountHolderName", nulls = Nulls.SKIP)
        public Builder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        @JsonSetter(value = "paymentIds", nulls = Nulls.SKIP)
        public Builder paymentIds(List<String> paymentIds) {
            this.paymentIds = paymentIds;
            return this;
        }

        public PaymentInfo build() {
            return new PaymentInfo(paymentType, par, cardProvider, lastFourDigits, iban, bic, bankName, accountHolderName, paymentIds);
        }
    }
}
