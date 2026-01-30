package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
public class Measure {

    @XmlValue
    private BigDecimal value;

    @XmlAttribute(name = "unitCode", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String unitCode;

    @XmlAttribute(name = "unitCodeListVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String unitCodeListVersionID;

    private Measure() {
    }

    public static class Builder {
        private final Measure measure = new Measure();

        public Builder(BigDecimal value, String unitCode) {
            if (value == null || unitCode == null) {
                throw new IllegalArgumentException("Value and unitCode are required");
            }
            measure.value = value;
            measure.unitCode = unitCode;
        }

        public Builder unitCodeListVersionID(String unitCodeListVersionID) {
            measure.unitCodeListVersionID = unitCodeListVersionID;
            return this;
        }

        public Measure build() {
            return measure;
        }
    }

    // Getters
    public BigDecimal getValue() {
        return value;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getUnitCodeListVersionID() {
        return unitCodeListVersionID;
    }

    @Override
    public String toString() {
        return "Measure{" +
                "value=" + value +
                ", unitCode='" + unitCode + '\'' +
                ", unitCodeListVersionID='" + unitCodeListVersionID + '\'' +
                '}';
    }
}