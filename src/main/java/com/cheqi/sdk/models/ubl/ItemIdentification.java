package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class ItemIdentification {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "ExtendedID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier extendedID;
    @XmlElement(name = "BarcodeSymbologyID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier barcodeSymbologyID;
    @XmlElement(name = "IssuerScopeID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier issuerScopeID;
    @XmlElement(name = "PhysicalAttribute", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<PhysicalAttribute> physicalAttribute;
    @XmlElement(name = "MeasurementDimension", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Dimension> measurementDimension;
    @XmlElement(name = "IssuerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party issuerParty;

    public ItemIdentification() {
    }

    private ItemIdentification(Builder builder) {
        this.id = builder.id;
        this.extendedID = builder.extendedID;
        this.barcodeSymbologyID = builder.barcodeSymbologyID;
        this.issuerScopeID = builder.issuerScopeID;
        this.physicalAttribute = builder.physicalAttribute;
        this.measurementDimension = builder.measurementDimension;
        this.issuerParty = builder.issuerParty;
    }

    public static class Builder {
        private final Identifier id;
        private Identifier extendedID;
        private Identifier barcodeSymbologyID;
        private Identifier issuerScopeID;
        private List<PhysicalAttribute> physicalAttribute;
        private List<Dimension> measurementDimension;
        private Party issuerParty;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder extendedID(Identifier extendedID) {
            this.extendedID = extendedID;
            return this;
        }

        public Builder barcodeSymbologyID(Identifier barcodeSymbologyID) {
            this.barcodeSymbologyID = barcodeSymbologyID;
            return this;
        }

        public Builder issuerScopeID(Identifier issuerScopeID) {
            this.issuerScopeID = issuerScopeID;
            return this;
        }

        public Builder physicalAttribute(List<PhysicalAttribute> physicalAttribute) {
            this.physicalAttribute = physicalAttribute;
            return this;
        }

        public Builder addPhysicalAttribute(PhysicalAttribute attribute) {
            if (this.physicalAttribute == null) {
                this.physicalAttribute = new ArrayList<>();
            }
            this.physicalAttribute.add(attribute);
            return this;
        }

        public Builder measurementDimension(List<Dimension> measurementDimension) {
            this.measurementDimension = measurementDimension;
            return this;
        }

        public Builder addMeasurementDimension(Dimension dimension) {
            if (this.measurementDimension == null) {
                this.measurementDimension = new ArrayList<>();
            }
            this.measurementDimension.add(dimension);
            return this;
        }

        public Builder issuerParty(Party issuerParty) {
            this.issuerParty = issuerParty;
            return this;
        }

        public ItemIdentification build() {
            return new ItemIdentification(this);
        }
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public Identifier getExtendedID() {
        return extendedID;
    }

    public Identifier getBarcodeSymbologyID() {
        return barcodeSymbologyID;
    }

    public Identifier getIssuerScopeID() {
        return issuerScopeID;
    }

    public List<PhysicalAttribute> getPhysicalAttribute() {
        return physicalAttribute;
    }

    public List<Dimension> getMeasurementDimension() {
        return measurementDimension;
    }

    public Party getIssuerParty() {
        return issuerParty;
    }
}
