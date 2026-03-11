package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class CreditAccount {
    @XmlElement(name = "AccountID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier accountID;

    public CreditAccount() {
    }

    private CreditAccount(CreditAccount.Builder builder) {
        this.accountID = builder.accountID;
    }

    public static class Builder {
        private Identifier accountID;

        public CreditAccount.Builder id(Identifier accountID) {
            this.accountID = accountID;
            return this;
        }

        public CreditAccount build() {
            return new CreditAccount(this);
        }
    }

    public Identifier getAccountID() {
        return accountID;
    }
}
