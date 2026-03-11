package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

//TODO: Still need to double check the allowance charge class
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class AllowanceCharge {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "ChargeIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Boolean chargeIndicator;
    @XmlElement(name = "AllowanceChargeReasonCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code allowanceChargeReasonCode;
    @XmlElement(name = "AllowanceChargeReason", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> allowanceChargeReason;
    @XmlElement(name = "MultiplierFactorNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Double multiplierFactorNumeric;
    @XmlElement(name = "PrepaidIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Boolean prepaidIndicator;
    @XmlElement(name = "SequenceNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Integer sequenceNumeric;
    @XmlElement(name = "Amount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount amount;
    @XmlElement(name = "TaxInclusiveAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxInclusiveAmount;
    @XmlElement(name = "BaseAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount baseAmount;
    @XmlElement(name = "AccountingCostCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code accountingCostCode;
    @XmlElement(name = "AccountingCost", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String accountingCost;
    @XmlElement(name = "PerUnitAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount perUnitAmount;
    @XmlElement(name = "TaxCategory", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private TaxCategory taxCategory;
    @XmlElement(name = "TaxTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private TaxTotal taxTotal;
    @XmlElement(name = "PaymentMeans", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<PaymentMeans> paymentMeans;

    private AllowanceCharge(Builder builder) {
        this.id = builder.id;
        this.chargeIndicator = builder.chargeIndicator;
        this.allowanceChargeReasonCode = builder.allowanceChargeReasonCode;
        this.allowanceChargeReason = builder.allowanceChargeReason;
        this.multiplierFactorNumeric = builder.multiplierFactorNumeric;
        this.prepaidIndicator = builder.prepaidIndicator;
        this.sequenceNumeric = builder.sequenceNumeric;
        this.amount = builder.amount;
        this.taxInclusiveAmount = builder.taxInclusiveAmount;
        this.baseAmount = builder.baseAmount;
        this.accountingCostCode = builder.accountingCostCode;
        this.accountingCost = builder.accountingCost;
        this.perUnitAmount = builder.perUnitAmount;
        this.taxCategory = builder.taxCategory;
        this.taxTotal = builder.taxTotal;
        this.paymentMeans = builder.paymentMeans;
    }

    public AllowanceCharge() {
    }

    public static class Builder {
        private final Boolean chargeIndicator;
        private final Amount amount;
        private Identifier id;
        private Code allowanceChargeReasonCode;
        private List<String> allowanceChargeReason = new ArrayList<>();
        private Double multiplierFactorNumeric;
        private Boolean prepaidIndicator;
        private Integer sequenceNumeric;
        private Amount taxInclusiveAmount;
        private Amount baseAmount;
        private Code accountingCostCode;
        private String accountingCost;
        private Amount perUnitAmount;
        private TaxCategory taxCategory;
        private TaxTotal taxTotal;
        private List<PaymentMeans> paymentMeans = new ArrayList<>();

        public Builder(Boolean chargeIndicator, Amount amount) {
            this.chargeIndicator = chargeIndicator;
            this.amount = amount;
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder allowanceChargeReasonCode(Code allowanceChargeReasonCode) {
            this.allowanceChargeReasonCode = allowanceChargeReasonCode;
            return this;
        }

        public Builder addAllowanceChargeReason(String reason) {
            this.allowanceChargeReason.add(reason);
            return this;
        }

        public Builder multiplierFactorNumeric(Double multiplierFactorNumeric) {
            this.multiplierFactorNumeric = multiplierFactorNumeric;
            return this;
        }

        public Builder prepaidIndicator(Boolean prepaidIndicator) {
            this.prepaidIndicator = prepaidIndicator;
            return this;
        }

        public Builder sequenceNumeric(Integer sequenceNumeric) {
            this.sequenceNumeric = sequenceNumeric;
            return this;
        }

        public Builder taxInclusiveAmount(Amount taxInclusiveAmount) {
            this.taxInclusiveAmount = taxInclusiveAmount;
            return this;
        }

        public Builder baseAmount(Amount baseAmount) {
            this.baseAmount = baseAmount;
            return this;
        }

        public Builder accountingCostCode(Code accountingCostCode) {
            this.accountingCostCode = accountingCostCode;
            return this;
        }

        public Builder accountingCost(String accountingCost) {
            this.accountingCost = accountingCost;
            return this;
        }

        public Builder perUnitAmount(Amount perUnitAmount) {
            this.perUnitAmount = perUnitAmount;
            return this;
        }

        public Builder taxCategory(TaxCategory taxCategory) {
            this.taxCategory = taxCategory;
            return this;
        }

        public Builder taxTotal(TaxTotal taxTotal) {
            this.taxTotal = taxTotal;
            return this;
        }

        public Builder addPaymentMeans(PaymentMeans paymentMeans) {
            this.paymentMeans.add(paymentMeans);
            return this;
        }

        public AllowanceCharge build() {
            return new AllowanceCharge(this);
        }
    }

    public static Builder builder(Boolean chargeIndicator, Amount amount) {
        return new Builder(chargeIndicator, amount);
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public Boolean isChargeIndicator() {
        return chargeIndicator;
    }

    public Code getAllowanceChargeReasonCode() {
        return allowanceChargeReasonCode;
    }

    public List<String> getAllowanceChargeReason() {
        return allowanceChargeReason;
    }

    public Double getMultiplierFactorNumeric() {
        return multiplierFactorNumeric;
    }

    public Boolean getPrepaidIndicator() {
        return prepaidIndicator;
    }

    public Integer getSequenceNumeric() {
        return sequenceNumeric;
    }

    public Amount getAmount() {
        return amount;
    }

    public Amount getTaxInclusiveAmount() {
        return taxInclusiveAmount;
    }

    public Amount getBaseAmount() {
        return baseAmount;
    }

    public Code getAccountingCostCode() {
        return accountingCostCode;
    }

    public String getAccountingCost() {
        return accountingCost;
    }

    public Amount getPerUnitAmount() {
        return perUnitAmount;
    }

    public TaxCategory getTaxCategory() {
        return taxCategory;
    }

    public TaxTotal getTaxTotal() {
        return taxTotal;
    }

    public List<PaymentMeans> getPaymentMeans() {
        return paymentMeans;
    }
}
