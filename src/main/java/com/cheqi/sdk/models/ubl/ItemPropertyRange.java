package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class ItemPropertyRange {
    @XmlElement(name = "MinimumValue", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String minimumValue;
    @XmlElement(name = "MaximumValue", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String maximumValue;

    public ItemPropertyRange() {
    }

    private ItemPropertyRange(Builder builder) {
        this.minimumValue = builder.minimumValue;
        this.maximumValue = builder.maximumValue;
    }

    public static class Builder {
        private String minimumValue;
        private String maximumValue;

        public Builder minimumValue(String minimumValue) {
            this.minimumValue = minimumValue;
            return this;
        }

        public Builder maximumValue(String maximumValue) {
            this.maximumValue = maximumValue;
            return this;
        }

        public ItemPropertyRange build() {
            return new ItemPropertyRange(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getMinimumValue() {
        return minimumValue;
    }

    public String getMaximumValue() {
        return maximumValue;
    }

    @Override
    public String toString() {
        return "ItemPropertyRange{" +
                "minimumValue='" + minimumValue + '\'' +
                ", maximumValue='" + maximumValue + '\'' +
                '}';
    }
}
