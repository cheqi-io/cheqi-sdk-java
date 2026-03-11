package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class TaxSubtotal {
    @XmlElement(name = "TaxableAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxableAmount;
    @XmlElement(name = "TaxAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxAmount;
    @XmlElement(name = "TaxInclusiveAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxInclusiveAmount;
    @XmlElement(name = "CalculationSequenceNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Integer calculationSequenceNumeric;
    @XmlElement(name = "TransactionCurrencyTaxAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount transactionCurrencyTaxAmount;
    @XmlElement(name = "Percent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Double percent;
    @XmlElement(name = "BaseUnitMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Measure baseUnitMeasure;
    @XmlElement(name = "PerUnitAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount perUnitAmount;
    @XmlElement(name = "TierRange", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String tierRange;
    @XmlElement(name = "TierRatePercent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Double tierRatePercent;
    @XmlElement(name = "TaxCategory", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private TaxCategory taxCategory;
    @XmlElement(name = "Country", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Country country;

    public TaxSubtotal() {
    }

    private TaxSubtotal(Builder builder) {
        this.taxableAmount = builder.taxableAmount;
        this.taxAmount = builder.taxAmount;
        this.taxInclusiveAmount = builder.taxInclusiveAmount;
        this.calculationSequenceNumeric = builder.calculationSequenceNumeric;
        this.transactionCurrencyTaxAmount = builder.transactionCurrencyTaxAmount;
        this.percent = builder.percent;
        this.baseUnitMeasure = builder.baseUnitMeasure;
        this.perUnitAmount = builder.perUnitAmount;
        this.tierRange = builder.tierRange;
        this.tierRatePercent = builder.tierRatePercent;
        this.taxCategory = builder.taxCategory;
        this.country = builder.country;
    }

    public static class Builder {
        private final Amount taxAmount;
        private final TaxCategory taxCategory;
        private Amount taxableAmount;
        private Amount taxInclusiveAmount;
        private Integer calculationSequenceNumeric;
        private Amount transactionCurrencyTaxAmount;
        private Double percent;
        private Measure baseUnitMeasure;
        private Amount perUnitAmount;
        private String tierRange;
        private Double tierRatePercent;
        private Country country;

        public Builder(Amount taxAmount, TaxCategory taxCategory) {
            this.taxAmount = taxAmount;
            this.taxCategory = taxCategory;
        }

        public Builder taxableAmount(Amount taxableAmount) {
            this.taxableAmount = taxableAmount;
            return this;
        }

        public Builder taxInclusiveAmount(Amount taxInclusiveAmount) {
            this.taxInclusiveAmount = taxInclusiveAmount;
            return this;
        }

        public Builder calculationSequenceNumeric(Integer calculationSequenceNumeric) {
            this.calculationSequenceNumeric = calculationSequenceNumeric;
            return this;
        }

        public Builder transactionCurrencyTaxAmount(Amount transactionCurrencyTaxAmount) {
            this.transactionCurrencyTaxAmount = transactionCurrencyTaxAmount;
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

        public Builder tierRange(String tierRange) {
            this.tierRange = tierRange;
            return this;
        }

        public Builder tierRatePercent(Double tierRatePercent) {
            this.tierRatePercent = tierRatePercent;
            return this;
        }

        public Builder country(Country country) {
            this.country = country;
            return this;
        }

        public TaxSubtotal build() {
            return new TaxSubtotal(this);
        }
    }

    public static Builder builder(Amount taxAmount, TaxCategory taxCategory) {
        return new Builder(taxAmount, taxCategory);
    }

    // Getters
    public Amount getTaxableAmount() {
        return taxableAmount;
    }

    public Amount getTaxAmount() {
        return taxAmount;
    }

    public Amount getTaxInclusiveAmount() {
        return taxInclusiveAmount;
    }

    public Integer getCalculationSequenceNumeric() {
        return calculationSequenceNumeric;
    }

    public Amount getTransactionCurrencyTaxAmount() {
        return transactionCurrencyTaxAmount;
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

    public String getTierRange() {
        return tierRange;
    }

    public Double getTierRatePercent() {
        return tierRatePercent;
    }

    public TaxCategory getTaxCategory() {
        return taxCategory;
    }

    public Country getCountry() {
        return country;
    }
}
