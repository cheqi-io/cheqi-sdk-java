package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PropertyIdentification {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private Identifier id;
    @XmlElement(name = "IssuerScopeID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier issuerScopeId;
    @XmlElement(name = "IssuerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party issuerParty;

    public PropertyIdentification() {
    }

    private PropertyIdentification(Builder builder) {
        this.id = builder.id;
        this.issuerScopeId = builder.issuerScopeId;
        this.issuerParty = builder.issuerParty;
    }

    public static class Builder {
        private final Identifier id;
        private Identifier issuerScopeId;
        private Party issuerParty;

        public Builder(Identifier id) {
            if (id == null) {
                throw new IllegalArgumentException("ID is mandatory and cannot be null");
            }
            this.id = id;
        }

        public Builder issuerScopeId(Identifier issuerScopeId) {
            this.issuerScopeId = issuerScopeId;
            return this;
        }

        public Builder issuerParty(Party issuerParty) {
            this.issuerParty = issuerParty;
            return this;
        }

        public PropertyIdentification build() {
            return new PropertyIdentification(this);
        }
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public Identifier getIssuerScopeId() {
        return issuerScopeId;
    }

    public Party getIssuerParty() {
        return issuerParty;
    }

    @Override
    public String toString() {
        return "PropertyIdentification{" +
                "id=" + id +
                ", issuerScopeId=" + issuerScopeId +
                ", issuerParty=" + issuerParty +
                '}';
    }
}
