package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class AddressLine {
    @XmlElement(name = "Line", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String line;

    public AddressLine() {
    }

    public AddressLine(String line) {
        this.line = line;
    }

    private AddressLine(Builder builder) {
        this.line = builder.line;
    }

    public static class Builder {
        private String line;

        public Builder(String line) {
            if (line == null) {
                throw new IllegalArgumentException("Line cannot be null");
            }
            this.line = line;
        }

        public AddressLine build() {
            return new AddressLine(this);
        }
    }

    public String getLine() {
        return line;
    }
}
