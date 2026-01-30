package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LocationCoordinate {
    @XmlElement(name = "CoordinateSystemCode")
    private Code coordinateSystemCode;

    @XmlElement(name = "LatitudeDegreesMeasure")
    private Measure latitudeDegreesMeasure;

    @XmlElement(name = "LatitudeMinutesMeasure")
    private Measure latitudeMinutesMeasure;

    @XmlElement(name = "LatitudeDirectionCode")
    private Code latitudeDirectionCode;

    @XmlElement(name = "LongitudeDegreesMeasure")
    private Measure longitudeDegreesMeasure;

    @XmlElement(name = "LongitudeMinutesMeasure")
    private Measure longitudeMinutesMeasure;

    @XmlElement(name = "LongitudeDirectionCode")
    private Code longitudeDirectionCode;

    @XmlElement(name = "AltitudeMeasure")
    private Measure altitudeMeasure;

    private LocationCoordinate() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocationCoordinate locationCoordinate;

        private Builder() {
            locationCoordinate = new LocationCoordinate();
        }

        public Builder coordinateSystemCode(Code code) {
            locationCoordinate.coordinateSystemCode = code;
            return this;
        }

        public Builder latitudeDegreesMeasure(Measure measure) {
            locationCoordinate.latitudeDegreesMeasure = measure;
            return this;
        }

        public Builder latitudeMinutesMeasure(Measure measure) {
            locationCoordinate.latitudeMinutesMeasure = measure;
            return this;
        }

        public Builder latitudeDirectionCode(Code code) {
            locationCoordinate.latitudeDirectionCode = code;
            return this;
        }

        public Builder longitudeDegreesMeasure(Measure measure) {
            locationCoordinate.longitudeDegreesMeasure = measure;
            return this;
        }

        public Builder longitudeMinutesMeasure(Measure measure) {
            locationCoordinate.longitudeMinutesMeasure = measure;
            return this;
        }

        public Builder longitudeDirectionCode(Code code) {
            locationCoordinate.longitudeDirectionCode = code;
            return this;
        }

        public Builder altitudeMeasure(Measure measure) {
            locationCoordinate.altitudeMeasure = measure;
            return this;
        }

        public LocationCoordinate build() {
            return locationCoordinate;
        }
    }

    // Getters
    public Code getCoordinateSystemCode() {
        return coordinateSystemCode;
    }

    public Measure getLatitudeDegreesMeasure() {
        return latitudeDegreesMeasure;
    }

    public Measure getLatitudeMinutesMeasure() {
        return latitudeMinutesMeasure;
    }

    public Code getLatitudeDirectionCode() {
        return latitudeDirectionCode;
    }

    public Measure getLongitudeDegreesMeasure() {
        return longitudeDegreesMeasure;
    }

    public Measure getLongitudeMinutesMeasure() {
        return longitudeMinutesMeasure;
    }

    public Code getLongitudeDirectionCode() {
        return longitudeDirectionCode;
    }

    public Measure getAltitudeMeasure() {
        return altitudeMeasure;
    }

    @Override
    public String toString() {
        return "LocationCoordinate{" +
                "coordinateSystemCode=" + coordinateSystemCode +
                ", latitudeDegreesMeasure=" + latitudeDegreesMeasure +
                ", latitudeMinutesMeasure=" + latitudeMinutesMeasure +
                ", latitudeDirectionCode=" + latitudeDirectionCode +
                ", longitudeDegreesMeasure=" + longitudeDegreesMeasure +
                ", longitudeMinutesMeasure=" + longitudeMinutesMeasure +
                ", longitudeDirectionCode=" + longitudeDirectionCode +
                ", altitudeMeasure=" + altitudeMeasure +
                '}';
    }
}
