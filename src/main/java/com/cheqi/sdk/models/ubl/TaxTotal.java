package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class TaxTotal {
    @XmlElement(name = "TaxAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxAmount;
    @XmlElement(name = "CalculationSequenceNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Integer calculationSequenceNumeric;
    @XmlElement(name = "RoundingAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount roundingAmount;
    @XmlElement(name = "TaxEvidenceIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Boolean taxEvidenceIndicator;
    @XmlElement(name = "TaxIncludedIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Boolean taxIncludedIndicator;
    @XmlElement(name = "TaxSubtotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<TaxSubtotal> taxSubtotals;

    private TaxTotal() {
    }

    private TaxTotal(Builder builder) {
        this.taxAmount = builder.taxAmount;
        this.calculationSequenceNumeric = builder.calculationSequenceNumeric;
        this.roundingAmount = builder.roundingAmount;
        this.taxEvidenceIndicator = builder.taxEvidenceIndicator;
        this.taxIncludedIndicator = builder.taxIncludedIndicator;
        this.taxSubtotals = builder.taxSubtotals;
    }

    public static class Builder {
        private final Amount taxAmount;
        private Integer calculationSequenceNumeric;
        private Amount roundingAmount;
        private Boolean taxEvidenceIndicator;
        private Boolean taxIncludedIndicator;
        private List<TaxSubtotal> taxSubtotals = new ArrayList<>();

        public Builder(Amount taxAmount) {
            this.taxAmount = taxAmount;
        }

        public Builder calculationSequenceNumeric(Integer calculationSequenceNumeric) {
            this.calculationSequenceNumeric = calculationSequenceNumeric;
            return this;
        }

        public Builder roundingAmount(Amount roundingAmount) {
            this.roundingAmount = roundingAmount;
            return this;
        }

        public Builder taxEvidenceIndicator(Boolean taxEvidenceIndicator) {
            this.taxEvidenceIndicator = taxEvidenceIndicator;
            return this;
        }

        public Builder taxIncludedIndicator(Boolean taxIncludedIndicator) {
            this.taxIncludedIndicator = taxIncludedIndicator;
            return this;
        }

        public Builder addTaxSubtotal(TaxSubtotal taxSubtotal) {
            this.taxSubtotals.add(taxSubtotal);
            return this;
        }

        public Builder taxSubtotals(List<TaxSubtotal> taxSubtotals) {
            this.taxSubtotals = taxSubtotals;
            return this;
        }

        public TaxTotal build() {
            return new TaxTotal(this);
        }
    }

    public static Builder builder(Amount taxAmount) {
        return new Builder(taxAmount);
    }

    // Getters
    public Amount getTaxAmount() {
        return taxAmount;
    }

    public Integer getCalculationSequenceNumeric() {
        return calculationSequenceNumeric;
    }

    public Amount getRoundingAmount() {
        return roundingAmount;
    }

    public Boolean getTaxEvidenceIndicator() {
        return taxEvidenceIndicator;
    }

    public Boolean getTaxIncludedIndicator() {
        return taxIncludedIndicator;
    }

    public List<TaxSubtotal> getTaxSubtotals() {
        return taxSubtotals;
    }

}
