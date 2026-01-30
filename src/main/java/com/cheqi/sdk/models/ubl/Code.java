package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
public class Code {

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String value;

    @XmlAttribute(name = "languageID")
    private String languageID;

    @XmlAttribute(name = "listAgencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String listAgencyID;

    @XmlAttribute(name = "listAgencyName")
    private String listAgencyName;

    @XmlAttribute(name = "listID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String listID;

    @XmlAttribute(name = "listName")
    private String listName;

    @XmlAttribute(name = "listSchemeURI")
    private String listSchemeURI;

    @XmlAttribute(name = "listURI")
    private String listURI;

    @XmlAttribute(name = "listVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String listVersionID;

    @XmlAttribute(name = "name")
    private String name;

    private Code() {
    }

    public static class Builder {
        private final Code code = new Code();

        public Builder(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Value is required");
            }
            code.value = value;
        }

        public Builder languageID(String languageID) {
            code.languageID = languageID;
            return this;
        }

        public Builder listAgencyID(String listAgencyID) {
            code.listAgencyID = listAgencyID;
            return this;
        }

        public Builder listAgencyName(String listAgencyName) {
            code.listAgencyName = listAgencyName;
            return this;
        }

        public Builder listID(String listID) {
            code.listID = listID;
            return this;
        }

        public Builder listName(String listName) {
            code.listName = listName;
            return this;
        }

        public Builder listSchemeURI(String listSchemeURI) {
            code.listSchemeURI = listSchemeURI;
            return this;
        }

        public Builder listURI(String listURI) {
            code.listURI = listURI;
            return this;
        }

        public Builder listVersionID(String listVersionID) {
            code.listVersionID = listVersionID;
            return this;
        }

        public Builder name(String name) {
            code.name = name;
            return this;
        }

        public Code build() {
            return code;
        }
    }

    // Getters
    public String getValue() {
        return value;
    }

    public String getLanguageID() {
        return languageID;
    }

    public String getListAgencyID() {
        return listAgencyID;
    }

    public String getListAgencyName() {
        return listAgencyName;
    }

    public String getListID() {
        return listID;
    }

    public String getListName() {
        return listName;
    }

    public String getListSchemeURI() {
        return listSchemeURI;
    }

    public String getListURI() {
        return listURI;
    }

    public String getListVersionID() {
        return listVersionID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Code{" +
                "value='" + value + '\'' +
                ", languageID='" + languageID + '\'' +
                ", listAgencyID='" + listAgencyID + '\'' +
                ", listAgencyName='" + listAgencyName + '\'' +
                ", listID='" + listID + '\'' +
                ", listName='" + listName + '\'' +
                ", listSchemeURI='" + listSchemeURI + '\'' +
                ", listURI='" + listURI + '\'' +
                ", listVersionID='" + listVersionID + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}