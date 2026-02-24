package com.cheqi.sdk.verification;

import com.cheqi.sdk.utils.HashUtils;
import com.cheqi.sdk.utils.RFC8785Canonicalizer;
import com.cheqi.sdk.utils.XMLCanonicalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Service for receipt integrity verification through canonicalization and hashing.
 *
 * Provides methods to compute deterministic hashes for both receipt formats:
 * <ul>
 *   <li><b>CheqiReceipt (JSON)</b>: RFC 8785 canonical JSON → SHA-256</li>
 *   <li><b>UBL XML</b>: Exclusive C14N 1.0 (W3C) → SHA-256</li>
 * </ul>
 *
 * These hashes can be submitted to the Cheqi backend for verification,
 * ensuring receipt integrity across all platforms (iOS, Java SDK, backend).
 */
public class VerificationService {
    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private final RFC8785Canonicalizer jsonCanonicalizer;
    private final XMLCanonicalService xmlCanonicalizer;

    public VerificationService() {
        this.jsonCanonicalizer = new RFC8785Canonicalizer();
        this.xmlCanonicalizer = new XMLCanonicalService();
        logger.debug("VerificationService initialized");
    }

    /**
     * Computes the SHA-256 hash of a CheqiReceipt JSON string after RFC 8785 canonicalization.
     *
     * @param cheqiReceiptJson the CheqiReceipt as a JSON string
     * @return lowercase hex-encoded SHA-256 hash
     * @throws IOException if JSON parsing fails
     */
    public String calculateCheqiReceiptHash(String cheqiReceiptJson) throws IOException {
        logger.debug("Calculating CheqiReceipt hash");
        return jsonCanonicalizer.calculateHash(cheqiReceiptJson);
    }

    /**
     * Computes the SHA-256 hash of a CheqiReceipt object after RFC 8785 canonicalization.
     *
     * @param cheqiReceipt the CheqiReceipt object
     * @return lowercase hex-encoded SHA-256 hash
     * @throws IOException if JSON serialization or parsing fails
     */
    public String calculateCheqiReceiptHash(Object cheqiReceipt) throws IOException {
        logger.debug("Calculating CheqiReceipt hash from object");
        return jsonCanonicalizer.calculateHash(cheqiReceipt);
    }

    /**
     * Computes the SHA-256 hash of a UBL XML string after Exclusive C14N 1.0 canonicalization.
     *
     * @param ublXml the UBL XML string
     * @return lowercase hex-encoded SHA-256 hash
     */
    public String calculateUblXmlHash(String ublXml) {
        logger.debug("Calculating UBL XML hash");
        return xmlCanonicalizer.calculateHash(ublXml);
    }

    /**
     * Canonicalizes a CheqiReceipt JSON string according to RFC 8785.
     *
     * @param cheqiReceiptJson the CheqiReceipt as a JSON string
     * @return the canonicalized JSON string
     * @throws IOException if JSON parsing fails
     */
    public String canonicalizeCheqiReceipt(String cheqiReceiptJson) throws IOException {
        return jsonCanonicalizer.canonicalize(cheqiReceiptJson);
    }

    /**
     * Canonicalizes a CheqiReceipt object according to RFC 8785.
     *
     * @param cheqiReceipt the CheqiReceipt object
     * @return the canonicalized JSON string
     * @throws IOException if JSON serialization or parsing fails
     */
    public String canonicalizeCheqiReceipt(Object cheqiReceipt) throws IOException {
        return jsonCanonicalizer.canonicalize(cheqiReceipt);
    }

    /**
     * Canonicalizes a UBL XML string using Exclusive C14N 1.0.
     *
     * @param ublXml the UBL XML string
     * @return the canonicalized XML string
     */
    public String canonicalizeUblXml(String ublXml) {
        return xmlCanonicalizer.canonicalize(ublXml);
    }
}
