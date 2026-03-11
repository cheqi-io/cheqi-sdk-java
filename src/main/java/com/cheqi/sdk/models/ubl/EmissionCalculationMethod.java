package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class EmissionCalculationMethod {
    @XmlElement(name = "CalculationMethodCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String calculationMethodCode;
    @XmlElement(name = "FullnessIndicationCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String fullnessIndicationCode;
    @XmlElement(name = "MeasurementFromLocation", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Location measurementFromLocation;
    @XmlElement(name = "MeasurementToLocation", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Location measurementToLocation;

    public EmissionCalculationMethod() {
    }

    private EmissionCalculationMethod(Builder builder) {
        this.calculationMethodCode = builder.calculationMethodCode;
        this.fullnessIndicationCode = builder.fullnessIndicationCode;
        this.measurementFromLocation = builder.measurementFromLocation;
        this.measurementToLocation = builder.measurementToLocation;
    }

    public static class Builder {
        private String calculationMethodCode;
        private String fullnessIndicationCode;
        private Location measurementFromLocation;
        private Location measurementToLocation;

        public Builder calculationMethodCode(String calculationMethodCode) {
            this.calculationMethodCode = calculationMethodCode;
            return this;
        }

        public Builder fullnessIndicationCode(String fullnessIndicationCode) {
            this.fullnessIndicationCode = fullnessIndicationCode;
            return this;
        }

        public Builder measurementFromLocation(Location measurementFromLocation) {
            this.measurementFromLocation = measurementFromLocation;
            return this;
        }

        public Builder measurementToLocation(Location measurementToLocation) {
            this.measurementToLocation = measurementToLocation;
            return this;
        }

        public EmissionCalculationMethod build() {
            return new EmissionCalculationMethod(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getCalculationMethodCode() {
        return calculationMethodCode;
    }

    public String getFullnessIndicationCode() {
        return fullnessIndicationCode;
    }

    public Location getMeasurementFromLocation() {
        return measurementFromLocation;
    }

    public Location getMeasurementToLocation() {
        return measurementToLocation;
    }

    @Override
    public String toString() {
        return "EmissionCalculationMethod{" +
                "calculationMethodCode='" + calculationMethodCode + '\'' +
                ", fullnessIndicationCode='" + fullnessIndicationCode + '\'' +
                ", measurementFromLocation=" + measurementFromLocation +
                ", measurementToLocation=" + measurementToLocation +
                '}';
    }
}
