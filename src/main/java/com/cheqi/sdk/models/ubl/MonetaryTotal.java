package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class MonetaryTotal {
    @XmlElement(name = "LineExtensionAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount lineExtensionAmount;
    @XmlElement(name = "TaxExclusiveAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxExclusiveAmount;
    @XmlElement(name = "TaxInclusiveAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxInclusiveAmount;
    @XmlElement(name = "AllowanceTotalAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount allowanceTotalAmount;
    @XmlElement(name = "AllowanceTotalTaxInclusiveAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount allowanceTotalTaxInclusiveAmount;
    @XmlElement(name = "ChargeTotalAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount chargeTotalAmount;
    @XmlElement(name = "ChargeTotalTaxInclusiveAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount chargeTotalTaxInclusiveAmount;
    @XmlElement(name = "WithholdingTaxTotalAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount withholdingTaxTotalAmount;
    @XmlElement(name = "PrepaidAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount prepaidAmount;
    @XmlElement(name = "PayableRoundingAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount payableRoundingAmount;
    @XmlElement(name = "PayableAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount payableAmount;
    @XmlElement(name = "PayableAlternativeAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount payableAlternativeAmount;

    public MonetaryTotal() {
    }

    private MonetaryTotal(Builder builder) {
        this.lineExtensionAmount = builder.lineExtensionAmount;
        this.taxExclusiveAmount = builder.taxExclusiveAmount;
        this.taxInclusiveAmount = builder.taxInclusiveAmount;
        this.allowanceTotalAmount = builder.allowanceTotalAmount;
        this.allowanceTotalTaxInclusiveAmount = builder.allowanceTotalTaxInclusiveAmount;
        this.chargeTotalAmount = builder.chargeTotalAmount;
        this.chargeTotalTaxInclusiveAmount = builder.chargeTotalTaxInclusiveAmount;
        this.withholdingTaxTotalAmount = builder.withholdingTaxTotalAmount;
        this.prepaidAmount = builder.prepaidAmount;
        this.payableRoundingAmount = builder.payableRoundingAmount;
        this.payableAmount = builder.payableAmount;
        this.payableAlternativeAmount = builder.payableAlternativeAmount;
    }

    public static class Builder {
        private final Amount lineExtensionAmount;
        private final Amount taxExclusiveAmount;
        private final Amount taxInclusiveAmount;
        private final Amount payableAmount;
        private Amount allowanceTotalAmount;
        private Amount allowanceTotalTaxInclusiveAmount;
        private Amount chargeTotalAmount;
        private Amount chargeTotalTaxInclusiveAmount;
        private Amount withholdingTaxTotalAmount;
        private Amount prepaidAmount;
        private Amount payableRoundingAmount;
        private Amount payableAlternativeAmount;

        public Builder(Amount lineExtensionAmount, Amount taxExclusiveAmount, Amount taxInclusiveAmount, Amount payableAmount) {
            this.lineExtensionAmount = lineExtensionAmount;
            this.taxExclusiveAmount = taxExclusiveAmount;
            this.taxInclusiveAmount = taxInclusiveAmount;
            this.payableAmount = payableAmount;
        }

        public Builder allowanceTotalAmount(Amount allowanceTotalAmount) {
            this.allowanceTotalAmount = allowanceTotalAmount;
            return this;
        }

        public Builder allowanceTotalTaxInclusiveAmount(Amount allowanceTotalTaxInclusiveAmount) {
            this.allowanceTotalTaxInclusiveAmount = allowanceTotalTaxInclusiveAmount;
            return this;
        }

        public Builder chargeTotalAmount(Amount chargeTotalAmount) {
            this.chargeTotalAmount = chargeTotalAmount;
            return this;
        }

        public Builder chargeTotalTaxInclusiveAmount(Amount chargeTotalTaxInclusiveAmount) {
            this.chargeTotalTaxInclusiveAmount = chargeTotalTaxInclusiveAmount;
            return this;
        }

        public Builder withholdingTaxTotalAmount(Amount withholdingTaxTotalAmount) {
            this.withholdingTaxTotalAmount = withholdingTaxTotalAmount;
            return this;
        }

        public Builder prepaidAmount(Amount prepaidAmount) {
            this.prepaidAmount = prepaidAmount;
            return this;
        }

        public Builder payableRoundingAmount(Amount payableRoundingAmount) {
            this.payableRoundingAmount = payableRoundingAmount;
            return this;
        }

        public Builder payableAlternativeAmount(Amount payableAlternativeAmount) {
            this.payableAlternativeAmount = payableAlternativeAmount;
            return this;
        }

        public MonetaryTotal build() {
            return new MonetaryTotal(this);
        }
    }

    public static Builder builder(Amount lineExtensionAmount, Amount taxExclusiveAmount, Amount taxInclusiveAmount, Amount payableAmount) {
        return new Builder(lineExtensionAmount, taxExclusiveAmount, taxInclusiveAmount, payableAmount);
    }

    // Getters
    public Amount getLineExtensionAmount() {
        return lineExtensionAmount;
    }

    public Amount getTaxExclusiveAmount() {
        return taxExclusiveAmount;
    }

    public Amount getTaxInclusiveAmount() {
        return taxInclusiveAmount;
    }

    public Amount getAllowanceTotalAmount() {
        return allowanceTotalAmount;
    }

    public Amount getAllowanceTotalTaxInclusiveAmount() {
        return allowanceTotalTaxInclusiveAmount;
    }

    public Amount getChargeTotalAmount() {
        return chargeTotalAmount;
    }

    public Amount getChargeTotalTaxInclusiveAmount() {
        return chargeTotalTaxInclusiveAmount;
    }

    public Amount getWithholdingTaxTotalAmount() {
        return withholdingTaxTotalAmount;
    }

    public Amount getPrepaidAmount() {
        return prepaidAmount;
    }

    public Amount getPayableRoundingAmount() {
        return payableRoundingAmount;
    }

    public Amount getPayableAmount() {
        return payableAmount;
    }

    public Amount getPayableAlternativeAmount() {
        return payableAlternativeAmount;
    }
}
