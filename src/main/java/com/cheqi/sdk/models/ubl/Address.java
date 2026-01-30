package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Address {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "AddressTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code addressTypeCode;
    @XmlElement(name = "AddressFormatCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code addressFormatCode;
    @XmlElement(name = "Postbox", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String postbox;
    @XmlElement(name = "Floor", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String floor;
    @XmlElement(name = "Room", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String room;
    @XmlElement(name = "StreetName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String streetName;
    @XmlElement(name = "AdditionalStreetName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String additionalStreetName;
    @XmlElement(name = "BlockName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String blockName;
    @XmlElement(name = "BuildingName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String buildingName;
    @XmlElement(name = "BuildingNumber", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String buildingNumber;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> description;
    @XmlElement(name = "InhouseMail", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String inhouseMail;
    @XmlElement(name = "Department", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String department;
    @XmlElement(name = "MarkAttention", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String markAttention;
    @XmlElement(name = "MarkCare", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String markCare;
    @XmlElement(name = "PlotIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String plotIdentification;
    @XmlElement(name = "CitySubdivisionName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String citySubdivisionName;
    @XmlElement(name = "CityName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String cityName;
    @XmlElement(name = "PostalZone", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String postalZone;
    @XmlElement(name = "CountrySubentity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String countrySubentity;
    @XmlElement(name = "CountrySubentityCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code countrySubentityCode;
    @XmlElement(name = "Region", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String region;
    @XmlElement(name = "District", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String district;
    @XmlElement(name = "TimezoneOffset", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String timezoneOffset;
    @XmlElement(name = "AddressLine", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<AddressLine> addressLine;
    @XmlElement(name = "Country", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Country country;
    @XmlElement(name = "LocationCoordinate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private LocationCoordinate locationCoordinate;

    private Address(Builder builder) {
        this.id = builder.id;
        this.addressTypeCode = builder.addressTypeCode;
        this.addressFormatCode = builder.addressFormatCode;
        this.postbox = builder.postbox;
        this.floor = builder.floor;
        this.room = builder.room;
        this.streetName = builder.streetName;
        this.additionalStreetName = builder.additionalStreetName;
        this.blockName = builder.blockName;
        this.buildingName = builder.buildingName;
        this.buildingNumber = builder.buildingNumber;
        this.description = builder.description;
        this.inhouseMail = builder.inhouseMail;
        this.department = builder.department;
        this.markAttention = builder.markAttention;
        this.markCare = builder.markCare;
        this.plotIdentification = builder.plotIdentification;
        this.citySubdivisionName = builder.citySubdivisionName;
        this.cityName = builder.cityName;
        this.postalZone = builder.postalZone;
        this.countrySubentity = builder.countrySubentity;
        this.countrySubentityCode = builder.countrySubentityCode;
        this.region = builder.region;
        this.district = builder.district;
        this.timezoneOffset = builder.timezoneOffset;
        this.addressLine = builder.addressLine;
        this.country = builder.country;
        this.locationCoordinate = builder.locationCoordinate;
    }

    public Address() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier id;
        private Code addressTypeCode;
        private Code addressFormatCode;
        private String postbox;
        private String floor;
        private String room;
        private String streetName;
        private String additionalStreetName;
        private String blockName;
        private String buildingName;
        private String buildingNumber;
        private List<String> description = new ArrayList<>();
        private String inhouseMail;
        private String department;
        private String markAttention;
        private String markCare;
        private String plotIdentification;
        private String citySubdivisionName;
        private String cityName;
        private String postalZone;
        private String countrySubentity;
        private Code countrySubentityCode;
        private String region;
        private String district;
        private String timezoneOffset;
        private List<AddressLine> addressLine = new ArrayList<>();
        private Country country;
        private LocationCoordinate locationCoordinate;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder addressTypeCode(Code addressTypeCode) {
            this.addressTypeCode = addressTypeCode;
            return this;
        }

        public Builder addressFormatCode(Code addressFormatCode) {
            this.addressFormatCode = addressFormatCode;
            return this;
        }

        public Builder postbox(String postbox) {
            this.postbox = postbox;
            return this;
        }

        public Builder floor(String floor) {
            this.floor = floor;
            return this;
        }

        public Builder room(String room) {
            this.room = room;
            return this;
        }

        public Builder streetName(String streetName) {
            this.streetName = streetName;
            return this;
        }

        public Builder additionalStreetName(String additionalStreetName) {
            this.additionalStreetName = additionalStreetName;
            return this;
        }

        public Builder blockName(String blockName) {
            this.blockName = blockName;
            return this;
        }

        public Builder buildingName(String buildingName) {
            this.buildingName = buildingName;
            return this;
        }

        public Builder buildingNumber(String buildingNumber) {
            this.buildingNumber = buildingNumber;
            return this;
        }

        public Builder addDescription(String description) {
            this.description.add(description);
            return this;
        }

        public Builder inhouseMail(String inhouseMail) {
            this.inhouseMail = inhouseMail;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder markAttention(String markAttention) {
            this.markAttention = markAttention;
            return this;
        }

        public Builder markCare(String markCare) {
            this.markCare = markCare;
            return this;
        }

        public Builder plotIdentification(String plotIdentification) {
            this.plotIdentification = plotIdentification;
            return this;
        }

        public Builder citySubdivisionName(String citySubdivisionName) {
            this.citySubdivisionName = citySubdivisionName;
            return this;
        }

        public Builder cityName(String cityName) {
            this.cityName = cityName;
            return this;
        }

        public Builder postalZone(String postalZone) {
            this.postalZone = postalZone;
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

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder district(String district) {
            this.district = district;
            return this;
        }

        public Builder timezoneOffset(String timezoneOffset) {
            this.timezoneOffset = timezoneOffset;
            return this;
        }

        public Builder addAddressLine(AddressLine addressLine) {
            this.addressLine.add(addressLine);
            return this;
        }

        public Builder country(Country country) {
            this.country = country;
            return this;
        }

        public Builder locationCoordinate(LocationCoordinate locationCoordinate) {
            this.locationCoordinate = locationCoordinate;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public Code getAddressTypeCode() {
        return addressTypeCode;
    }

    public Code getAddressFormatCode() {
        return addressFormatCode;
    }

    public String getPostbox() {
        return postbox;
    }

    public String getFloor() {
        return floor;
    }

    public String getRoom() {
        return room;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getAdditionalStreetName() {
        return additionalStreetName;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getInhouseMail() {
        return inhouseMail;
    }

    public String getDepartment() {
        return department;
    }

    public String getMarkAttention() {
        return markAttention;
    }

    public String getMarkCare() {
        return markCare;
    }

    public String getPlotIdentification() {
        return plotIdentification;
    }

    public String getCitySubdivisionName() {
        return citySubdivisionName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPostalZone() {
        return postalZone;
    }

    public String getCountrySubentity() {
        return countrySubentity;
    }

    public Code getCountrySubentityCode() {
        return countrySubentityCode;
    }

    public String getRegion() {
        return region;
    }

    public String getDistrict() {
        return district;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public List<AddressLine> getAddressLine() {
        return addressLine;
    }

    public Country getCountry() {
        return country;
    }

    public LocationCoordinate getLocationCoordinate() {
        return locationCoordinate;
    }
}