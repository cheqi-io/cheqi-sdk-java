package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlElement;

public class CashRegister {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "SerialNumberID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier serialNumberID;

    private CashRegister() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CashRegister cashRegister;

        private Builder() {
            cashRegister = new CashRegister();
        }

        public Builder id(Identifier id) {
            cashRegister.id = id;
            return this;
        }

        public Builder serialNumberID(Identifier serialNumberID) {
            cashRegister.serialNumberID = serialNumberID;
            return this;
        }

        public CashRegister build() {
            return cashRegister;
        }
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public Identifier getSerialNumberID() {
        return serialNumberID;
    }
}