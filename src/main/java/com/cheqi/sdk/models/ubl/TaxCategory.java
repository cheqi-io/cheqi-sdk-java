package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class TaxCategory {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Name name;
    @XmlElement(name = "Percent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Double percent;
    @XmlElement(name = "BaseUnitMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Measure baseUnitMeasure;
    @XmlElement(name = "PerUnitAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount perUnitAmount;
    @XmlElement(name = "TaxExemptionReasonCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code taxExemptionReasonCode;
    @XmlElement(name = "TaxExemptionReason", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> taxExemptionReason;
    @XmlElement(name = "TierRange", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String tierRange;
    @XmlElement(name = "TierRatePercent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Double tierRatePercent;
    @XmlElement(name = "TaxScheme")
    private TaxScheme taxScheme;

    public TaxCategory() {
    }

    private TaxCategory(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.percent = builder.percent;
        this.baseUnitMeasure = builder.baseUnitMeasure;
        this.perUnitAmount = builder.perUnitAmount;
        this.taxExemptionReasonCode = builder.taxExemptionReasonCode;
        this.taxExemptionReason = builder.taxExemptionReason;
        this.tierRange = builder.tierRange;
        this.tierRatePercent = builder.tierRatePercent;
        this.taxScheme = builder.taxScheme;
    }

    public static class Builder {
        private final TaxScheme taxScheme;
        private Identifier id;
        private Name name;
        private Double percent;
        private Measure baseUnitMeasure;
        private Amount perUnitAmount;
        private Code taxExemptionReasonCode;
        private List<String> taxExemptionReason;
        private String tierRange;
        private Double tierRatePercent;

        public Builder(TaxScheme taxScheme) {
            this.taxScheme = taxScheme;
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder percent(Double percent) {
            this.percent = percent;
            return this;
        }

        public Builder baseUnitMeasure(Measure baseUnitMeasure) {
            this.baseUnitMeasure = baseUnitMeasure;
            return this;
        }

        public Builder perUnitAmount(Amount perUnitAmount) {
            this.perUnitAmount = perUnitAmount;
            return this;
        }

        public Builder taxExemptionReasonCode(Code taxExemptionReasonCode) {
            this.taxExemptionReasonCode = taxExemptionReasonCode;
            return this;
        }

        public Builder taxExemptionReason(List<String> taxExemptionReason) {
            this.taxExemptionReason = taxExemptionReason;
            return this;
        }

        public Builder tierRange(String tierRange) {
            this.tierRange = tierRange;
            return this;
        }

        public Builder tierRatePercent(Double tierRatePercent) {
            this.tierRatePercent = tierRatePercent;
            return this;
        }

        public TaxCategory build() {
            return new TaxCategory(this);
        }
    }

    public static Builder builder(TaxScheme taxScheme) {
        return new Builder(taxScheme);
    }

    public Identifier getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Double getPercent() {
        return percent;
    }

    public Measure getBaseUnitMeasure() {
        return baseUnitMeasure;
    }

    public Amount getPerUnitAmount() {
        return perUnitAmount;
    }

    public Code getTaxExemptionReasonCode() {
        return taxExemptionReasonCode;
    }

    public List<String> getTaxExemptionReason() {
        return taxExemptionReason;
    }

    public String getTierRange() {
        return tierRange;
    }

    public Double getTierRatePercent() {
        return tierRatePercent;
    }

    public TaxScheme getTaxScheme() {
        return taxScheme;
    }
}
