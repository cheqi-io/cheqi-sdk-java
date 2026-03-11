package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Period {
    @XmlElement(name = "StartDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final String startDate;
    @XmlElement(name = "StartTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final String startTime;
    @XmlElement(name = "EndDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final String endDate;
    @XmlElement(name = "EndTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final String endTime;
    @XmlElement(name = "DurationMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final Measure durationMeasure;
    @XmlElement(name = "DescriptionCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final Code descriptionCode;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private final String description;

    private Period(Builder builder) {
        this.startDate = builder.startDate;
        this.startTime = builder.startTime;
        this.endDate = builder.endDate;
        this.endTime = builder.endTime;
        this.durationMeasure = builder.durationMeasure;
        this.descriptionCode = builder.descriptionCode;
        this.description = builder.description;
    }

    public static class Builder {
        private String startDate;
        private String startTime;
        private String endDate;
        private String endTime;
        private Measure durationMeasure;
        private Code descriptionCode;
        private String description;

        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder durationMeasure(Measure durationMeasure) {
            this.durationMeasure = durationMeasure;
            return this;
        }

        public Builder descriptionCode(Code descriptionCode) {
            this.descriptionCode = descriptionCode;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Period build() {
            return new Period(this);
        }
    }

    // Getters
    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public Measure getDurationMeasure() {
        return durationMeasure;
    }

    public Code getDescriptionCode() {
        return descriptionCode;
    }

    public String getDescription() {
        return description;
    }
}