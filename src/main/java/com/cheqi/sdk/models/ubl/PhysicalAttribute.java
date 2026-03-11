package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PhysicalAttribute {
    @XmlElement(name = "AttributeID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier attributeID;
    @XmlElement(name = "PositionCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code positionCode;
    @XmlElement(name = "DescriptionCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code descriptionCode;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> description;

    public PhysicalAttribute() {
    }

    private PhysicalAttribute(Builder builder) {
        this.attributeID = builder.attributeID;
        this.positionCode = builder.positionCode;
        this.descriptionCode = builder.descriptionCode;
        this.description = builder.description;
    }

    public static class Builder {
        private final Identifier attributeID;
        private Code positionCode;
        private Code descriptionCode;
        private List<String> description = new ArrayList<>();

        public Builder(Identifier attributeID) {
            this.attributeID = attributeID;
        }

        public Builder positionCode(Code positionCode) {
            this.positionCode = positionCode;
            return this;
        }

        public Builder descriptionCode(Code descriptionCode) {
            this.descriptionCode = descriptionCode;
            return this;
        }

        public Builder addDescription(String desc) {
            this.description.add(desc);
            return this;
        }

        public Builder descriptions(List<String> descriptions) {
            this.description = new ArrayList<>(descriptions);
            return this;
        }

        public PhysicalAttribute build() {
            return new PhysicalAttribute(this);
        }
    }

    public static Builder builder(Identifier attributeID) {
        return new Builder(attributeID);
    }

    // Getters
    public Identifier getAttributeID() {
        return attributeID;
    }

    public Code getPositionCode() {
        return positionCode;
    }

    public Code getDescriptionCode() {
        return descriptionCode;
    }

    public List<String> getDescription() {
        return description;
    }
}
