package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.*;

import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
public class Amount {
    @XmlAttribute(name = "currencyID")
    private String currencyID;
    @XmlValue
    private BigDecimal value;

    public Amount() {
        // no-arg constructor for JAXB
    }

    public Amount(String currencyID, BigDecimal value) {
        this.currencyID = currencyID;
        this.value = value;
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setCurrencyID(String currencyID) {
        this.currencyID = currencyID;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
