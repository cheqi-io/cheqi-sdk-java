package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class DocumentDistribution {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "DocumentTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code documentTypeCode;
    @XmlElement(name = "DistributionTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code distributionTypeCode;
    @XmlElement(name = "DistributionType", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> distributionType;
    @XmlElement(name = "PrintQualifier", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String printQualifier;
    @XmlElement(name = "CopyIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private boolean copyIndicator;
    @XmlElement(name = "MaximumCopiesNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private double maximumCopiesNumeric;
    @XmlElement(name = "MaximumOriginalsNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private double maximumOriginalsNumeric;
    @XmlElement(name = "Communication", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Communication communication;
    @XmlElement(name = "Party", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party party;

    public DocumentDistribution() {
    }

    private DocumentDistribution(Builder builder) {
        this.id = builder.id;
        this.documentTypeCode = builder.documentTypeCode;
        this.distributionTypeCode = builder.distributionTypeCode;
        this.distributionType = builder.distributionType;
        this.printQualifier = builder.printQualifier;
        this.copyIndicator = builder.copyIndicator;
        this.maximumCopiesNumeric = builder.maximumCopiesNumeric;
        this.maximumOriginalsNumeric = builder.maximumOriginalsNumeric;
        this.communication = builder.communication;
        this.party = builder.party;
    }

    public static class Builder {
        private final Party party;
        private Identifier id;
        private Code documentTypeCode;
        private Code distributionTypeCode;
        private List<String> distributionType;
        private String printQualifier;
        private boolean copyIndicator;
        private double maximumCopiesNumeric;
        private double maximumOriginalsNumeric;
        private Communication communication;

        public Builder(Party party) {
            this.party = party;
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder documentTypeCode(Code documentTypeCode) {
            this.documentTypeCode = documentTypeCode;
            return this;
        }

        public Builder distributionTypeCode(Code distributionTypeCode) {
            this.distributionTypeCode = distributionTypeCode;
            return this;
        }

        public Builder distributionType(List<String> distributionType) {
            this.distributionType = distributionType;
            return this;
        }

        public Builder printQualifier(String printQualifier) {
            this.printQualifier = printQualifier;
            return this;
        }

        public Builder copyIndicator(boolean copyIndicator) {
            this.copyIndicator = copyIndicator;
            return this;
        }

        public Builder maximumCopiesNumeric(double maximumCopiesNumeric) {
            this.maximumCopiesNumeric = maximumCopiesNumeric;
            return this;
        }

        public Builder maximumOriginalsNumeric(double maximumOriginalsNumeric) {
            this.maximumOriginalsNumeric = maximumOriginalsNumeric;
            return this;
        }

        public Builder communication(Communication communication) {
            this.communication = communication;
            return this;
        }

        public DocumentDistribution build() {
            return new DocumentDistribution(this);
        }
    }

    public static Builder builder(Party party) {
        return new Builder(party);
    }

    public Identifier getId() {
        return id;
    }

    public Code getDocumentTypeCode() {
        return documentTypeCode;
    }

    public Code getDistributionTypeCode() {
        return distributionTypeCode;
    }

    public List<String> getDistributionType() {
        return distributionType;
    }

    public String getPrintQualifier() {
        return printQualifier;
    }

    public boolean isCopyIndicator() {
        return copyIndicator;
    }

    public double getMaximumCopiesNumeric() {
        return maximumCopiesNumeric;
    }

    public double getMaximumOriginalsNumeric() {
        return maximumOriginalsNumeric;
    }

    public Communication getCommunication() {
        return communication;
    }

    public Party getParty() {
        return party;
    }
}
