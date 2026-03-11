package com.cheqi.sdk.models.ubl;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "PurchaseReceipt", namespace = "urn:oasis:names:specification:ubl:schema:xsd:PurchaseReceipt-2")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PurchaseReceipt {
    @XmlElement(name = "CustomizationID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier customizationID;
    @XmlElement(name = "ProfileID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier profileID;
    @XmlElement(name = "ProfileExecutionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier profileExecutionID;
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "UUID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier uuid;
    @XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String issueDate;
    @XmlElement(name = "IssueTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String issueTime;
    @XmlElement(name = "TransactionDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String transactionDate;
    @XmlElement(name = "TransactionTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String transactionTime;
    @XmlElement(name = "PurchaseDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String purchaseDate;
    @XmlElement(name = "PurchaseTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String purchaseTime;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String note;
    @XmlElement(name = "DocumentCurrencyCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String documentCurrencyCode;
    @XmlElement(name = "PurchaseReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private PurchaseReference purchaseReference;
    @XmlElement(name = "SalesDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private DocumentReference salesDocumentReference;
    @XmlElement(name = "AdditionalDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private DocumentReference additionalDocumentReference;
    @XmlElement(name = "Signature", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Signature signature;
    @XmlElement(name = "AccountingSupplierParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party accountingSupplierParty;
    @XmlElement(name = "AccountingCustomerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party accountingCustomerParty;
    @XmlElement(name = "CashierContact", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Contact cashierContact;
    @XmlElement(name = "ShopperContact", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private CashRegister cashRegister;
    @XmlElement(name = "PointOfSaleLocation", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Location pointOfSaleLocation;
    @XmlElement(name = "PointOfSaleContact", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Contact pointOfSaleContact;
    @XmlElement(name = "Payment", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Payment payment;
    @XmlElement(name = "PaymentMeans", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<PaymentMeans> paymentMeans;
    @XmlElement(name = "AllowanceCharge", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<AllowanceCharge> allowanceCharge;
    @XmlElement(name = "TaxTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<TaxTotal> taxTotals;
    @XmlElement(name = "LegalMonetaryTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private MonetaryTotal legalMonetaryTotal;
    @XmlElement(name = "PurchaseReceiptLine", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<PurchaseReceiptLine> purchaseReceiptLines;
    @XmlElement(name = "VerificationNonce", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String verificationNonce;

    public PurchaseReceipt() {
    }

    private PurchaseReceipt(Builder builder) {
        this.id = builder.id;
        this.issueDate = builder.issueDate;
        this.accountingSupplierParty = builder.accountingSupplierParty;
        this.legalMonetaryTotal = builder.legalMonetaryTotal;
        this.purchaseReceiptLines = builder.purchaseReceiptLines;
        this.customizationID = builder.customizationID;
        this.profileID = builder.profileID;
        this.profileExecutionID = builder.profileExecutionID;
        this.uuid = builder.uuid;
        this.issueTime = builder.issueTime;
        this.transactionDate = builder.transactionDate;
        this.transactionTime = builder.transactionTime;
        this.purchaseDate = builder.purchaseDate;
        this.purchaseTime = builder.purchaseTime;
        this.note = builder.note;
        this.documentCurrencyCode = builder.documentCurrencyCode;
        this.purchaseReference = builder.purchaseReference;
        this.salesDocumentReference = builder.salesDocumentReference;
        this.additionalDocumentReference = builder.additionalDocumentReference;
        this.signature = builder.signature;
        this.accountingCustomerParty = builder.accountingCustomerParty;
        this.cashierContact = builder.cashierContact;
        this.cashRegister = builder.cashRegister;
        this.pointOfSaleLocation = builder.pointOfSaleLocation;
        this.pointOfSaleContact = builder.pointOfSaleContact;
        this.payment = builder.payment;
        this.paymentMeans = builder.paymentMeans;
        this.allowanceCharge = builder.allowanceCharge;
        this.taxTotals = builder.taxTotals;
        this.verificationNonce = builder.verificationNonce;
    }

    public static class Builder {
        private final Identifier id;
        private final String issueDate;
        private final Party accountingSupplierParty;
        private final MonetaryTotal legalMonetaryTotal;
        private final List<PurchaseReceiptLine> purchaseReceiptLines;

        private Identifier customizationID;
        private Identifier profileID;
        private Identifier profileExecutionID;
        private Identifier uuid;
        private String issueTime;
        private String transactionDate;
        private String transactionTime;
        private String purchaseDate;
        private String purchaseTime;
        private String note;
        private String documentCurrencyCode;
        private PurchaseReference purchaseReference;
        private DocumentReference salesDocumentReference;
        private DocumentReference additionalDocumentReference;
        private Signature signature;
        private Party accountingCustomerParty;
        private Contact cashierContact;
        private CashRegister cashRegister;
        private Location pointOfSaleLocation;
        private Contact pointOfSaleContact;
        private Payment payment;
        private List<PaymentMeans> paymentMeans;
        private List<AllowanceCharge> allowanceCharge;
        private List<TaxTotal> taxTotals;
        private String verificationNonce;

        public Builder(Identifier id, String issueDate, Party accountingSupplierParty, String documentCurrencyCode, MonetaryTotal legalMonetaryTotal, List<PurchaseReceiptLine> purchaseReceiptLines) {
            this.id = id;
            this.issueDate = issueDate;
            this.accountingSupplierParty = accountingSupplierParty;
            this.legalMonetaryTotal = legalMonetaryTotal;
            this.purchaseReceiptLines = purchaseReceiptLines;
            this.documentCurrencyCode = documentCurrencyCode;
        }

        public Builder customizationID(Identifier customizationID) {
            this.customizationID = customizationID;
            return this;
        }

        public Builder profileID(Identifier profileID) {
            this.profileID = profileID;
            return this;
        }

        public Builder profileExecutionID(Identifier profileExecutionID) {
            this.profileExecutionID = profileExecutionID;
            return this;
        }

        public Builder uuid(Identifier uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder issueTime(String issueTime) {
            this.issueTime = issueTime;
            return this;
        }

        public Builder transactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder transactionTime(String transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public Builder purchaseDate(String purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder purchaseTime(String purchaseTime) {
            this.purchaseTime = purchaseTime;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder documentCurrencyCode(String documentCurrencyCode) {
            this.documentCurrencyCode = documentCurrencyCode;
            return this;
        }

        public Builder purchaseReference(PurchaseReference purchaseReference) {
            this.purchaseReference = purchaseReference;
            return this;
        }

        public Builder salesDocumentReference(DocumentReference salesDocumentReference) {
            this.salesDocumentReference = salesDocumentReference;
            return this;
        }

        public Builder additionalDocumentReference(DocumentReference additionalDocumentReference) {
            this.additionalDocumentReference = additionalDocumentReference;
            return this;
        }

        public Builder signature(Signature signature) {
            this.signature = signature;
            return this;
        }

        public Builder accountingCustomerParty(Party accountingCustomerParty) {
            this.accountingCustomerParty = accountingCustomerParty;
            return this;
        }

        public Builder cashierContact(Contact cashierContact) {
            this.cashierContact = cashierContact;
            return this;
        }

        public Builder cashRegister(CashRegister cashRegister) {
            this.cashRegister = cashRegister;
            return this;
        }

        public Builder pointOfSaleLocation(Location pointOfSaleLocation) {
            this.pointOfSaleLocation = pointOfSaleLocation;
            return this;
        }

        public Builder pointOfSaleContact(Contact pointOfSaleContact) {
            this.pointOfSaleContact = pointOfSaleContact;
            return this;
        }

        public Builder payment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Builder paymentMeans(List<PaymentMeans> paymentMeans) {
            this.paymentMeans = paymentMeans;
            return this;
        }

        public Builder allowanceCharge(List<AllowanceCharge> allowanceCharge) {
            this.allowanceCharge = allowanceCharge;
            return this;
        }

        public Builder taxTotals(List<TaxTotal> taxTotals) {
            this.taxTotals = taxTotals;
            return this;
        }

        public Builder verificationNonce(String verificationNonce) {
            this.verificationNonce = verificationNonce;
            return this;
        }

        public PurchaseReceipt build() {
            return new PurchaseReceipt(this);
        }
    }

    public Identifier getCustomizationID() {
        return customizationID;
    }

    public void setCustomizationID(Identifier customizationID) {
        this.customizationID = customizationID;
    }

    public Identifier getProfileID() {
        return profileID;
    }

    public void setProfileID(Identifier profileID) {
        this.profileID = profileID;
    }

    public Identifier getProfileExecutionID() {
        return profileExecutionID;
    }

    public void setProfileExecutionID(Identifier profileExecutionID) {
        this.profileExecutionID = profileExecutionID;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Identifier getUuid() {
        return uuid;
    }

    public void setUuid(Identifier uuid) {
        this.uuid = uuid;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDocumentCurrencyCode() {
        return documentCurrencyCode;
    }

    public void setDocumentCurrencyCode(String documentCurrencyCode) {
        this.documentCurrencyCode = documentCurrencyCode;
    }

    public PurchaseReference getPurchaseReference() {
        return purchaseReference;
    }

    public void setPurchaseReference(PurchaseReference purchaseReference) {
        this.purchaseReference = purchaseReference;
    }

    public DocumentReference getSalesDocumentReference() {
        return salesDocumentReference;
    }

    public void setSalesDocumentReference(DocumentReference salesDocumentReference) {
        this.salesDocumentReference = salesDocumentReference;
    }

    public DocumentReference getAdditionalDocumentReference() {
        return additionalDocumentReference;
    }

    public void setAdditionalDocumentReference(DocumentReference additionalDocumentReference) {
        this.additionalDocumentReference = additionalDocumentReference;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public Party getAccountingSupplierParty() {
        return accountingSupplierParty;
    }

    public void setAccountingSupplierParty(Party accountingSupplierParty) {
        this.accountingSupplierParty = accountingSupplierParty;
    }

    public Party getAccountingCustomerParty() {
        return accountingCustomerParty;
    }

    public void setAccountingCustomerParty(Party accountingCustomerParty) {
        this.accountingCustomerParty = accountingCustomerParty;
    }

    public Contact getCashierContact() {
        return cashierContact;
    }

    public void setCashierContact(Contact cashierContact) {
        this.cashierContact = cashierContact;
    }

    public CashRegister getCashRegister() {
        return cashRegister;
    }

    public void setCashRegister(CashRegister cashRegister) {
        this.cashRegister = cashRegister;
    }

    public Location getPointOfSaleLocation() {
        return pointOfSaleLocation;
    }

    public void setPointOfSaleLocation(Location pointOfSaleLocation) {
        this.pointOfSaleLocation = pointOfSaleLocation;
    }

    public Contact getPointOfSaleContact() {
        return pointOfSaleContact;
    }

    public void setPointOfSaleContact(Contact pointOfSaleContact) {
        this.pointOfSaleContact = pointOfSaleContact;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<PaymentMeans> getPaymentMeans() {
        return paymentMeans;
    }

    public void setPaymentMeans(List<PaymentMeans> paymentMeans) {
        this.paymentMeans = paymentMeans;
    }

    public List<AllowanceCharge> getAllowanceCharge() {
        return allowanceCharge;
    }

    public void setAllowanceCharge(List<AllowanceCharge> allowanceCharge) {
        this.allowanceCharge = allowanceCharge;
    }

    public List<TaxTotal> getTaxTotals() {
        return taxTotals;
    }

    public void setTaxTotals(List<TaxTotal> taxTotals) {
        this.taxTotals = taxTotals;
    }

    public MonetaryTotal getLegalMonetaryTotal() {
        return legalMonetaryTotal;
    }

    public void setLegalMonetaryTotal(MonetaryTotal legalMonetaryTotal) {
        this.legalMonetaryTotal = legalMonetaryTotal;
    }

    public List<PurchaseReceiptLine> getPurchaseReceiptLines() {
        return purchaseReceiptLines;
    }

    public void setPurchaseReceiptLines(List<PurchaseReceiptLine> purchaseReceiptLines) {
        this.purchaseReceiptLines = purchaseReceiptLines;
    }

    public String getVerificationNonce() {
        return verificationNonce;
    }

    public void setVerificationNonce(String verificationNonce) {
        this.verificationNonce = verificationNonce;
    }

    @Override
    public String toString() {
        return "PurchaseReceipt{" +
                "customizationID=" + customizationID +
                ", profileID=" + profileID +
                ", profileExecutionID=" + profileExecutionID +
                ", id=" + id +
                ", uuid=" + uuid +
                ", issueDate='" + issueDate + '\'' +
                ", issueTime='" + issueTime + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", transactionTime='" + transactionTime + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", purchaseTime='" + purchaseTime + '\'' +
                ", note='" + note + '\'' +
                ", documentCurrencyCode='" + documentCurrencyCode + '\'' +
                ", purchaseReference=" + purchaseReference +
                ", salesDocumentReference=" + salesDocumentReference +
                ", additionalDocumentReference=" + additionalDocumentReference +
                ", signature=" + signature +
                ", accountingSupplierParty=" + accountingSupplierParty +
                ", accountingCustomerParty=" + accountingCustomerParty +
                ", cashierContact=" + cashierContact +
                ", cashRegister=" + cashRegister +
                ", pointOfSaleLocation=" + pointOfSaleLocation +
                ", pointOfSaleContact=" + pointOfSaleContact +
                ", payment=" + payment +
                ", paymentMeans=" + paymentMeans +
                ", allowanceCharge=" + allowanceCharge +
                ", taxTotals=" + taxTotals +
                ", legalMonetaryTotal=" + legalMonetaryTotal +
                ", purchaseReceiptLines=" + purchaseReceiptLines +
                ", verificationNonce='" + verificationNonce + '\'' +
                '}';
    }
}
