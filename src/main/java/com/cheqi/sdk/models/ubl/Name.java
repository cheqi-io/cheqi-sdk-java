package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Name {
    @XmlValue
    private String value;

    @XmlAttribute(name = "languageID")
    @XmlSchemaType(name = "language")
    private String languageID;

    @XmlAttribute(name = "languageLocaleID")
    @XmlSchemaType(name = "normalizedString")
    private String languageLocaleID;

    public Name() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Name name;

        private Builder() {
            name = new Name();
        }

        public Builder value(String value) {
            name.value = value;
            return this;
        }

        public Builder languageID(String languageID) {
            name.languageID = languageID;
            return this;
        }

        public Builder languageLocaleID(String languageLocaleID) {
            name.languageLocaleID = languageLocaleID;
            return this;
        }

        public Name build() {
            return name;
        }
    }

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getLanguageLocaleID() {
        return languageLocaleID;
    }

    public void setLanguageLocaleID(String languageLocaleID) {
        this.languageLocaleID = languageLocaleID;
    }

    @Override
    public String toString() {
        return "Name{" +
                "value='" + value + '\'' +
                ", languageID='" + languageID + '\'' +
                ", languageLocaleID='" + languageLocaleID + '\'' +
                '}';
    }
}