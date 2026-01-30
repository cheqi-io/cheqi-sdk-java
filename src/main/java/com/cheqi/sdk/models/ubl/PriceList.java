package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PriceList {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "StatusCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code statusCode;
    @XmlElement(name = "ValidityPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Period> validityPeriod;

    public PriceList() {
    }

    private PriceList(Builder builder) {
        this.id = builder.id;
        this.statusCode = builder.statusCode;
        this.validityPeriod = builder.validityPeriod;
    }

    public static class Builder {
        private Identifier id;
        private Code statusCode;
        private List<Period> validityPeriod;

        public Builder() {
            // No mandatory fields, so the constructor is empty
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder statusCode(Code statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder validityPeriod(List<Period> validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public PriceList build() {
            return new PriceList(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public Code getStatusCode() {
        return statusCode;
    }

    public List<Period> getValidityPeriod() {
        return validityPeriod;
    }

    @Override
    public String toString() {
        return "PriceList{" +
                "id=" + id +
                ", statusCode=" + statusCode +
                ", validityPeriod=" + validityPeriod +
                '}';
    }
}
