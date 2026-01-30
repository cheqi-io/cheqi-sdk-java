package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundBankAccount {
    @JsonProperty("iban")
    private String iban;
    @JsonProperty("bic")
    private String bic;
    @JsonProperty("accountHolder")
    private String accountHolder;

    public RefundBankAccount() {
    }

    public RefundBankAccount(String iban, String bic, String accountHolder) {
        this.iban = iban;
        this.bic = bic;
        this.accountHolder = accountHolder;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
}