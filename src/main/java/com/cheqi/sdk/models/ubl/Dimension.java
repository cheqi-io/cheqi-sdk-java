package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Dimension {
    @XmlElement(name = "AttributeID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier attributeID;
    @XmlElement(name = "Measure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Measure measure;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String description;
    @XmlElement(name = "MinimumMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Measure minimumMeasure;
    @XmlElement(name = "MaximumMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Measure maximumMeasure;

    public Dimension() {
    }

    private Dimension(Builder builder) {
        this.attributeID = builder.attributeID;
        this.measure = builder.measure;
        this.description = builder.description;
        this.minimumMeasure = builder.minimumMeasure;
        this.maximumMeasure = builder.maximumMeasure;
    }

    public static class Builder {
        private final Identifier attributeID;
        private Measure measure;
        private String description;
        private Measure minimumMeasure;
        private Measure maximumMeasure;

        public Builder(Identifier attributeID) {
            this.attributeID = attributeID;
        }

        public Builder measure(Measure measure) {
            this.measure = measure;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder minimumMeasure(Measure minimumMeasure) {
            this.minimumMeasure = minimumMeasure;
            return this;
        }

        public Builder maximumMeasure(Measure maximumMeasure) {
            this.maximumMeasure = maximumMeasure;
            return this;
        }

        public Dimension build() {
            return new Dimension(this);
        }
    }

    public static Builder builder(Identifier attributeID) {
        return new Builder(attributeID);
    }

    // Getters
    public Identifier getAttributeID() {
        return attributeID;
    }

    public Measure getMeasure() {
        return measure;
    }

    public String getDescription() {
        return description;
    }

    public Measure getMinimumMeasure() {
        return minimumMeasure;
    }

    public Measure getMaximumMeasure() {
        return maximumMeasure;
    }
}
