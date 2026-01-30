package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class EnvironmentalEmission {
    @XmlElement(name = "EnvironmentalEmissionTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private String environmentalEmissionTypeCode;

    @XmlElement(name = "ValueMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private Measure valueMeasure;

    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> description;

    @XmlElement(name = "EmissionCalculationMethod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<EmissionCalculationMethod> emissionCalculationMethod;

    public EnvironmentalEmission() {
    }

    private EnvironmentalEmission(Builder builder) {
        this.environmentalEmissionTypeCode = builder.environmentalEmissionTypeCode;
        this.valueMeasure = builder.valueMeasure;
        this.description = builder.description;
        this.emissionCalculationMethod = builder.emissionCalculationMethod;
    }

    public static class Builder {
        private final String environmentalEmissionTypeCode;
        private final Measure valueMeasure;
        private List<String> description;
        private List<EmissionCalculationMethod> emissionCalculationMethod;

        public Builder(String environmentalEmissionTypeCode, Measure valueMeasure) {
            if (environmentalEmissionTypeCode == null || environmentalEmissionTypeCode.trim().isEmpty()) {
                throw new IllegalArgumentException("EnvironmentalEmissionTypeCode is mandatory and cannot be null or empty");
            }
            if (valueMeasure == null) {
                throw new IllegalArgumentException("ValueMeasure is mandatory and cannot be null");
            }
            this.environmentalEmissionTypeCode = environmentalEmissionTypeCode;
            this.valueMeasure = valueMeasure;
        }

        public Builder description(List<String> description) {
            this.description = description;
            return this;
        }

        public Builder emissionCalculationMethod(List<EmissionCalculationMethod> emissionCalculationMethod) {
            this.emissionCalculationMethod = emissionCalculationMethod;
            return this;
        }

        public EnvironmentalEmission build() {
            return new EnvironmentalEmission(this);
        }
    }

    public static Builder builder(String environmentalEmissionTypeCode, Measure valueMeasure) {
        return new Builder(environmentalEmissionTypeCode, valueMeasure);
    }

    // Getters
    public String getEnvironmentalEmissionTypeCode() {
        return environmentalEmissionTypeCode;
    }

    public Measure getValueMeasure() {
        return valueMeasure;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<EmissionCalculationMethod> getEmissionCalculationMethod() {
        return emissionCalculationMethod;
    }

    @Override
    public String toString() {
        return "EnvironmentalEmission{" +
                "environmentalEmissionTypeCode='" + environmentalEmissionTypeCode + '\'' +
                ", valueMeasure=" + valueMeasure +
                ", description=" + description +
                ", emissionCalculationMethod=" + emissionCalculationMethod +
                '}';
    }
}
