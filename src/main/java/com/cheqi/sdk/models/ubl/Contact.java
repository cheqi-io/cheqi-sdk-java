package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:AggregatedComponents-2")
public class Contact {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String name;
    @XmlElement(name = "JobTitle", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String jobTitle;
    @XmlElement(name = "Department", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String department;
    @XmlElement(name = "Telephone", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String telephone;
    @XmlElement(name = "Telefax", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String telefax;
    @XmlElement(name = "ElectronicMail", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String electronicMail;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String note;
    @XmlElement(name = "OtherCommunication", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Communication> otherCommunication = new ArrayList<>();

    public Contact() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Contact contact;

        private Builder() {
            contact = new Contact();
        }

        public Builder id(Identifier id) {
            contact.id = id;
            return this;
        }

        public Builder name(String name) {
            contact.name = name;
            return this;
        }

        public Builder jobTitle(String jobTitle) {
            contact.jobTitle = jobTitle;
            return this;
        }

        public Builder department(String department) {
            contact.department = department;
            return this;
        }

        public Builder telephone(String telephone) {
            contact.telephone = telephone;
            return this;
        }

        public Builder telefax(String telefax) {
            contact.telefax = telefax;
            return this;
        }

        public Builder electronicMail(String electronicMail) {
            contact.electronicMail = electronicMail;
            return this;
        }

        public Builder note(String note) {
            contact.note = note;
            return this;
        }

        public Builder addOtherCommunication(Communication communication) {
            contact.otherCommunication.add(communication);
            return this;
        }

        public Builder otherCommunication(List<Communication> otherCommunication) {
            contact.otherCommunication = otherCommunication;
            return this;
        }

        public Contact build() {
            return contact;
        }
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getTelefax() {
        return telefax;
    }

    public String getElectronicMail() {
        return electronicMail;
    }

    public String getNote() {
        return note;
    }

    public List<Communication> getOtherCommunication() {
        return otherCommunication;
    }
}