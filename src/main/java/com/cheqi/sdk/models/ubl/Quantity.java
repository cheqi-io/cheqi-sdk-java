package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Quantity {
    @XmlValue
    private BigDecimal value;

    @XmlAttribute(name = "unitCode")
    private String unitCode;

    @XmlAttribute(name = "unitCodeListAgencyID")
    private String unitCodeListAgencyID;

    @XmlAttribute(name = "unitCodeListAgencyName")
    private String unitCodeListAgencyName;

    @XmlAttribute(name = "unitCodeListID")
    private String unitCodeListID;

    public Quantity() {
    }

    private Quantity(Builder builder) {
        this.value = builder.value;
        this.unitCode = builder.unitCode;
        this.unitCodeListAgencyID = builder.unitCodeListAgencyID;
        this.unitCodeListAgencyName = builder.unitCodeListAgencyName;
        this.unitCodeListID = builder.unitCodeListID;
    }

    public static class Builder {
        private final BigDecimal value;
        private String unitCode;
        private String unitCodeListAgencyID;
        private String unitCodeListAgencyName;
        private String unitCodeListID;

        public Builder(BigDecimal value) {
            this.value = value;
        }

        public Builder unitCode(String unitCode) {
            this.unitCode = unitCode;
            return this;
        }

        public Builder unitCodeListAgencyID(String unitCodeListAgencyID) {
            this.unitCodeListAgencyID = unitCodeListAgencyID;
            return this;
        }

        public Builder unitCodeListAgencyName(String unitCodeListAgencyName) {
            this.unitCodeListAgencyName = unitCodeListAgencyName;
            return this;
        }

        public Builder unitCodeListID(String unitCodeListID) {
            this.unitCodeListID = unitCodeListID;
            return this;
        }

        public Quantity build() {
            return new Quantity(this);
        }
    }

    public static Builder builder(BigDecimal value) {
        return new Builder(value);
    }

    // Getters
    public BigDecimal getValue() {
        return value;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getUnitCodeListAgencyID() {
        return unitCodeListAgencyID;
    }

    public String getUnitCodeListAgencyName() {
        return unitCodeListAgencyName;
    }

    public String getUnitCodeListID() {
        return unitCodeListID;
    }

}
