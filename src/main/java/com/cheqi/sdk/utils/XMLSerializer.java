package com.cheqi.sdk.utils;

import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.StringWriter;

public class XMLSerializer {
    public static String serializeToXml(PurchaseReceipt receipt) throws CheqiSDKException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PurchaseReceipt.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(receipt, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new CheqiSDKException("Failed to serialize receipt to XML", e);
        }
    }
}
