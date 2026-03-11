package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Identifier {
    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String value;

    @XmlAttribute(name = "schemeAgencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String schemeAgencyID;

    @XmlAttribute(name = "schemeAgencyName")
    private String schemeAgencyName;

    @XmlAttribute(name = "schemeDataURI")
    private String schemeDataURI;

    @XmlAttribute(name = "schemeID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String schemeID;

    @XmlAttribute(name = "schemeName")
    private String schemeName;

    @XmlAttribute(name = "schemeURI")
    private String schemeURI;

    @XmlAttribute(name = "schemeVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String schemeVersionID;

    // Private constructor for the builder
    private Identifier() {
    }

    public static class Builder {
        private Identifier identifier = new Identifier();

        public Builder(String value) {
            identifier.value = value;
        }

        public Builder schemeAgencyID(String schemeAgencyID) {
            identifier.schemeAgencyID = schemeAgencyID;
            return this;
        }

        public Builder schemeAgencyName(String schemeAgencyName) {
            identifier.schemeAgencyName = schemeAgencyName;
            return this;
        }

        public Builder schemeDataURI(String schemeDataURI) {
            identifier.schemeDataURI = schemeDataURI;
            return this;
        }

        public Builder schemeID(String schemeID) {
            identifier.schemeID = schemeID;
            return this;
        }

        public Builder schemeName(String schemeName) {
            identifier.schemeName = schemeName;
            return this;
        }

        public Builder schemeURI(String schemeURI) {
            identifier.schemeURI = schemeURI;
            return this;
        }

        public Builder schemeVersionID(String schemeVersionID) {
            identifier.schemeVersionID = schemeVersionID;
            return this;
        }

        public Identifier build() {
            return identifier;
        }
    }

    // Getters
    public String getValue() {
        return value;
    }

    public String getSchemeAgencyID() {
        return schemeAgencyID;
    }

    public String getSchemeAgencyName() {
        return schemeAgencyName;
    }

    public String getSchemeDataURI() {
        return schemeDataURI;
    }

    public String getSchemeID() {
        return schemeID;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public String getSchemeURI() {
        return schemeURI;
    }

    public String getSchemeVersionID() {
        return schemeVersionID;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "value='" + value + '\'' +
                ", schemeAgencyID='" + schemeAgencyID + '\'' +
                ", schemeAgencyName='" + schemeAgencyName + '\'' +
                ", schemeDataURI='" + schemeDataURI + '\'' +
                ", schemeID='" + schemeID + '\'' +
                ", schemeName='" + schemeName + '\'' +
                ", schemeURI='" + schemeURI + '\'' +
                ", schemeVersionID='" + schemeVersionID + '\'' +
                '}';
    }
}