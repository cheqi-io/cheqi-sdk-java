package com.cheqi.sdk.utils;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.models.ubl.PurchaseReceipt;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Utility class for transforming receipt data between JSON and XML formats.
 * 
 * This utility provides static methods to convert JSON string representations
 * of PurchaseReceipt objects into UBL-compliant XML format using JAXB marshalling.
 * 
 * Example usage:
 * <pre>
 * String jsonReceipt = "{\"id\":{\"value\":\"RCP-001\"},...}";
 * String xmlReceipt = ReceiptTransformationService.transformJsonToXml(jsonReceipt);
 * </pre>
 */
public final class ReceiptTransformationService {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperConfig.getInstance();


    // Private constructor to prevent instantiation
    private ReceiptTransformationService() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Transforms a JSON string representation of a PurchaseReceipt into UBL-compliant XML.
     * 
     * @param jsonReceipt JSON string representation of a PurchaseReceipt
     * @return UBL-compliant XML string
     * @throws CheqiSDKException if JSON parsing or XML marshalling fails
     */
    public static String transformJsonToXml(String jsonReceipt) throws CheqiSDKException {
        if (jsonReceipt == null || jsonReceipt.trim().isEmpty()) {
            throw new CheqiSDKException("JSON receipt string cannot be null or empty");
        }

        try {
            PurchaseReceipt receipt = OBJECT_MAPPER.readValue(jsonReceipt, PurchaseReceipt.class);
            return transformReceiptToXml(receipt);
        } catch (Exception e) {
            throw new CheqiSDKException("Failed to transform JSON to XML: " + e.getMessage(), e);
        }
    }

    /**
     * Transforms a PurchaseReceipt object into UBL-compliant XML.
     * 
     * @param receipt PurchaseReceipt object to transform
     * @return UBL-compliant XML string
     * @throws CheqiSDKException if XML marshalling fails
     */
    public static String transformReceiptToXml(PurchaseReceipt receipt) throws CheqiSDKException {
        if (receipt == null) {
            throw new CheqiSDKException("PurchaseReceipt cannot be null");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(PurchaseReceipt.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter writer = new StringWriter();
            marshaller.marshal(receipt, writer);
            return writer.toString();

        } catch (JAXBException e) {
            throw new CheqiSDKException("Failed to marshal PurchaseReceipt to XML: " + e.getMessage(), e);
        }
    }

    /**
     * Parses a JSON string into a PurchaseReceipt object.
     * 
     * @param jsonReceipt JSON string representation of a PurchaseReceipt
     * @return PurchaseReceipt object
     * @throws CheqiSDKException if JSON parsing fails
     */
    public static PurchaseReceipt parseJsonToReceipt(String jsonReceipt) throws CheqiSDKException {
        if (jsonReceipt == null || jsonReceipt.trim().isEmpty()) {
            throw new CheqiSDKException("JSON receipt string cannot be null or empty");
        }

        try {
            return OBJECT_MAPPER.readValue(jsonReceipt, PurchaseReceipt.class);
        } catch (Exception e) {
            throw new CheqiSDKException("Failed to parse JSON to PurchaseReceipt: " + e.getMessage(), e);
        }
    }

    public static String parseXmlToJson(String xmlReceipt) throws CheqiSDKException {
        if (xmlReceipt == null || xmlReceipt.trim().isEmpty()) {
            throw new CheqiSDKException("XML receipt string cannot be null or empty");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(PurchaseReceipt.class);
            return OBJECT_MAPPER.writeValueAsString(
                    context.createUnmarshaller()
                            .unmarshal(new java.io.StringReader(xmlReceipt))
            );
        } catch (Exception e) {
            throw new CheqiSDKException("Failed to parse XML to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a PurchaseReceipt object to JSON string.
     * 
     * @param receipt PurchaseReceipt object to serialize
     * @return JSON string representation
     * @throws CheqiSDKException if JSON serialization fails
     */
    public static String serializeReceiptToJson(PurchaseReceipt receipt) throws CheqiSDKException {
        if (receipt == null) {
            throw new CheqiSDKException("PurchaseReceipt cannot be null");
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(receipt);
        } catch (Exception e) {
            throw new CheqiSDKException("Failed to serialize PurchaseReceipt to JSON: " + e.getMessage(), e);
        }
    }
}
