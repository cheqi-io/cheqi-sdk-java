@XmlSchema(
        namespace = "urn:oasis:names:specification:ubl:schema:xsd:PurchaseReceipt-2",
        xmlns = {
                @XmlNs(prefix = "", namespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:PurchaseReceipt-2"),
                @XmlNs(prefix = "cac", namespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"),
                @XmlNs(prefix = "cbc", namespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
        },
        elementFormDefault = XmlNsForm.QUALIFIED
)
package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;