package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Price {
    @XmlElement(name = "PriceAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private Amount priceAmount;

    @XmlElement(name = "TaxInclusivePriceAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxInclusivePriceAmount;

    @XmlElement(name = "BaseQuantity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Quantity baseQuantity;

    @XmlElement(name = "PriceChangeReason", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> priceChangeReason;

    @XmlElement(name = "PriceTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code priceTypeCode;

    @XmlElement(name = "PriceType", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String priceType;

    @XmlElement(name = "OrderableUnitFactorRate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private BigDecimal orderableUnitFactorRate;

    @XmlElement(name = "ValidityPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Period> validityPeriod;

    @XmlElement(name = "PriceList", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private PriceList priceList;

    @XmlElement(name = "AllowanceCharge", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<AllowanceCharge> allowanceCharge;

    public Price() {
    }

    private Price(Builder builder) {
        this.priceAmount = builder.priceAmount;
        this.taxInclusivePriceAmount = builder.taxInclusivePriceAmount;
        this.baseQuantity = builder.baseQuantity;
        this.priceChangeReason = builder.priceChangeReason;
        this.priceTypeCode = builder.priceTypeCode;
        this.priceType = builder.priceType;
        this.orderableUnitFactorRate = builder.orderableUnitFactorRate;
        this.validityPeriod = builder.validityPeriod;
        this.priceList = builder.priceList;
        this.allowanceCharge = builder.allowanceCharge;
    }

    public static class Builder {
        private final Amount priceAmount;
        private Amount taxInclusivePriceAmount;
        private Quantity baseQuantity;
        private List<String> priceChangeReason;
        private Code priceTypeCode;
        private String priceType;
        private BigDecimal orderableUnitFactorRate;
        private List<Period> validityPeriod;
        private PriceList priceList;
        private List<AllowanceCharge> allowanceCharge;

        public Builder(Amount priceAmount) {
            if (priceAmount == null) {
                throw new IllegalArgumentException("PriceAmount is mandatory and cannot be null");
            }
            this.priceAmount = priceAmount;
        }

        public Builder taxInclusivePriceAmount(Amount taxInclusivePriceAmount) {
            this.taxInclusivePriceAmount = taxInclusivePriceAmount;
            return this;
        }

        public Builder baseQuantity(Quantity baseQuantity) {
            this.baseQuantity = baseQuantity;
            return this;
        }

        public Builder priceChangeReason(List<String> priceChangeReason) {
            this.priceChangeReason = priceChangeReason;
            return this;
        }

        public Builder priceTypeCode(Code priceTypeCode) {
            this.priceTypeCode = priceTypeCode;
            return this;
        }

        public Builder priceType(String priceType) {
            this.priceType = priceType;
            return this;
        }

        public Builder orderableUnitFactorRate(BigDecimal orderableUnitFactorRate) {
            this.orderableUnitFactorRate = orderableUnitFactorRate;
            return this;
        }

        public Builder validityPeriod(List<Period> validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public Builder priceList(PriceList priceList) {
            this.priceList = priceList;
            return this;
        }

        public Builder allowanceCharge(List<AllowanceCharge> allowanceCharge) {
            this.allowanceCharge = allowanceCharge;
            return this;
        }

        public Price build() {
            return new Price(this);
        }
    }

    // Getters for all fields

    public Amount getPriceAmount() {
        return priceAmount;
    }

    public Amount getTaxInclusivePriceAmount() {
        return taxInclusivePriceAmount;
    }

    public Quantity getBaseQuantity() {
        return baseQuantity;
    }

    public List<String> getPriceChangeReason() {
        return priceChangeReason;
    }

    public Code getPriceTypeCode() {
        return priceTypeCode;
    }

    public String getPriceType() {
        return priceType;
    }

    public BigDecimal getOrderableUnitFactorRate() {
        return orderableUnitFactorRate;
    }

    public List<Period> getValidityPeriod() {
        return validityPeriod;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public List<AllowanceCharge> getAllowanceCharge() {
        return allowanceCharge;
    }
}
