package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class CommodityClassification {
    @XmlElement(name = "NatureCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String natureCode;

    @XmlElement(name = "CargoTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String cargoTypeCode;

    @XmlElement(name = "CommodityCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String commodityCode;

    @XmlElement(name = "ItemClassificationCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code itemClassificationCode;

    public CommodityClassification() {
    }

    private CommodityClassification(Builder builder) {
        this.natureCode = builder.natureCode;
        this.cargoTypeCode = builder.cargoTypeCode;
        this.commodityCode = builder.commodityCode;
        this.itemClassificationCode = builder.itemClassificationCode;
    }

    public static class Builder {
        private String natureCode;
        private String cargoTypeCode;
        private String commodityCode;
        private Code itemClassificationCode;

        public Builder natureCode(String natureCode) {
            this.natureCode = natureCode;
            return this;
        }

        public Builder cargoTypeCode(String cargoTypeCode) {
            this.cargoTypeCode = cargoTypeCode;
            return this;
        }

        public Builder commodityCode(String commodityCode) {
            this.commodityCode = commodityCode;
            return this;
        }

        public Builder itemClassificationCode(Code itemClassificationCode) {
            this.itemClassificationCode = itemClassificationCode;
            return this;
        }

        public CommodityClassification build() {
            return new CommodityClassification(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getNatureCode() {
        return natureCode;
    }

    public String getCargoTypeCode() {
        return cargoTypeCode;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public Code getItemClassificationCode() {
        return itemClassificationCode;
    }

    @Override
    public String toString() {
        return "CommodityClassification{" +
                "natureCode='" + natureCode + '\'' +
                ", cargoTypeCode='" + cargoTypeCode + '\'' +
                ", commodityCode='" + commodityCode + '\'' +
                ", itemClassificationCode='" + itemClassificationCode + '\'' +
                '}';
    }
}
