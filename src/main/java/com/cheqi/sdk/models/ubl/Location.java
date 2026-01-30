package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> description;
    @XmlElement(name = "Conditions", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> conditions;
    @XmlElement(name = "CountrySubentity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String countrySubentity;
    @XmlElement(name = "CountrySubentityCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code countrySubentityCode;
    @XmlElement(name = "LocationTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code locationTypeCode;
    @XmlElement(name = "InformationURI", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier informationURI;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Name name;
    @XmlElement(name = "ValidityPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Period> validityPeriod;
    @XmlElement(name = "Address", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Address address;
    @XmlElement(name = "LocationCoordinate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<LocationCoordinate> locationCoordinate;

    public Location() {
    }

    private Location(Builder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.conditions = builder.conditions;
        this.countrySubentity = builder.countrySubentity;
        this.countrySubentityCode = builder.countrySubentityCode;
        this.locationTypeCode = builder.locationTypeCode;
        this.informationURI = builder.informationURI;
        this.name = builder.name;
        this.validityPeriod = builder.validityPeriod;
        this.address = builder.address;
        this.locationCoordinate = builder.locationCoordinate;
    }

    public static class Builder {
        private Identifier id;
        private List<String> description;
        private List<String> conditions;
        private String countrySubentity;
        private Code countrySubentityCode;
        private Code locationTypeCode;
        private Identifier informationURI;
        private Name name;
        private List<Period> validityPeriod;
        private Address address;
        private List<LocationCoordinate> locationCoordinate;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder description(List<String> description) {
            this.description = description;
            return this;
        }

        public Builder conditions(List<String> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder countrySubentity(String countrySubentity) {
            this.countrySubentity = countrySubentity;
            return this;
        }

        public Builder countrySubentityCode(Code countrySubentityCode) {
            this.countrySubentityCode = countrySubentityCode;
            return this;
        }

        public Builder locationTypeCode(Code locationTypeCode) {
            this.locationTypeCode = locationTypeCode;
            return this;
        }

        public Builder informationURI(Identifier informationURI) {
            this.informationURI = informationURI;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder validityPeriod(List<Period> validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder locationCoordinate(List<LocationCoordinate> locationCoordinate) {
            this.locationCoordinate = locationCoordinate;
            return this;
        }

        public Location build() {
            return new Location(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Identifier getId() {
        return id;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public String getCountrySubentity() {
        return countrySubentity;
    }

    public Code getCountrySubentityCode() {
        return countrySubentityCode;
    }

    public Code getLocationTypeCode() {
        return locationTypeCode;
    }

    public Identifier getInformationURI() {
        return informationURI;
    }

    public Name getName() {
        return name;
    }

    public List<Period> getValidityPeriod() {
        return validityPeriod;
    }

    public Address getAddress() {
        return address;
    }

    public List<LocationCoordinate> getLocationCoordinate() {
        return locationCoordinate;
    }
}
