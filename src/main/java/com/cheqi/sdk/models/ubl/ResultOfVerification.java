package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class ResultOfVerification {
    @XmlElement(name = "ValidatorID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier validatorID;
    @XmlElement(name = "ValidationResultCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String validationResultCode;
    @XmlElement(name = "ValidationDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String validationDate;
    @XmlElement(name = "ValidationTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String validationTime;
    @XmlElement(name = "ValidateProcess", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String validateProcess;
    @XmlElement(name = "ValidateTool", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String validateTool;
    @XmlElement(name = "ValidateToolVersion", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String validateToolVersion;
    @XmlElement(name = "SignatoryParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party signatoryParty;

    public ResultOfVerification() {
    }

    private ResultOfVerification(Builder builder) {
        this.validatorID = builder.validatorID;
        this.validationResultCode = builder.validationResultCode;
        this.validationDate = builder.validationDate;
        this.validationTime = builder.validationTime;
        this.validateProcess = builder.validateProcess;
        this.validateTool = builder.validateTool;
        this.validateToolVersion = builder.validateToolVersion;
        this.signatoryParty = builder.signatoryParty;
    }

    public static class Builder {
        private Identifier validatorID;
        private String validationResultCode;
        private String validationDate;
        private String validationTime;
        private String validateProcess;
        private String validateTool;
        private String validateToolVersion;
        private Party signatoryParty;

        public Builder validatorID(Identifier validatorID) {
            this.validatorID = validatorID;
            return this;
        }

        public Builder validationResultCode(String validationResultCode) {
            this.validationResultCode = validationResultCode;
            return this;
        }

        public Builder validationDate(String validationDate) {
            this.validationDate = validationDate;
            return this;
        }

        public Builder validationTime(String validationTime) {
            this.validationTime = validationTime;
            return this;
        }

        public Builder validateProcess(String validateProcess) {
            this.validateProcess = validateProcess;
            return this;
        }

        public Builder validateTool(String validateTool) {
            this.validateTool = validateTool;
            return this;
        }

        public Builder validateToolVersion(String validateToolVersion) {
            this.validateToolVersion = validateToolVersion;
            return this;
        }

        public Builder signatoryParty(Party signatoryParty) {
            this.signatoryParty = signatoryParty;
            return this;
        }

        public ResultOfVerification build() {
            return new ResultOfVerification(this);
        }
    }

    public Identifier getValidatorID() {
        return validatorID;
    }

    public void setValidatorID(Identifier validatorID) {
        this.validatorID = validatorID;
    }

    public String getValidationResultCode() {
        return validationResultCode;
    }

    public void setValidationResultCode(String validationResultCode) {
        this.validationResultCode = validationResultCode;
    }

    public String getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(String validationDate) {
        this.validationDate = validationDate;
    }

    public String getValidationTime() {
        return validationTime;
    }

    public void setValidationTime(String validationTime) {
        this.validationTime = validationTime;
    }

    public String getValidateProcess() {
        return validateProcess;
    }

    public void setValidateProcess(String validateProcess) {
        this.validateProcess = validateProcess;
    }

    public String getValidateTool() {
        return validateTool;
    }

    public void setValidateTool(String validateTool) {
        this.validateTool = validateTool;
    }

    public String getValidateToolVersion() {
        return validateToolVersion;
    }

    public void setValidateToolVersion(String validateToolVersion) {
        this.validateToolVersion = validateToolVersion;
    }

    public Party getSignatoryParty() {
        return signatoryParty;
    }

    public void setSignatoryParty(Party signatoryParty) {
        this.signatoryParty = signatoryParty;
    }
}