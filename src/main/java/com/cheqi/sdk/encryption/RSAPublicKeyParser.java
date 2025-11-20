package com.cheqi.sdk.encryption;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Parses RSA public keys from various formats using BouncyCastle.
 * Supports both X.509 SubjectPublicKeyInfo format and raw iOS format.
 */
public class RSAPublicKeyParser {
    private static final Logger logger = LoggerFactory.getLogger(RSAPublicKeyParser.class);
    
    /**
     * Parses a base64-encoded RSA public key string.
     * Handles both X.509 SubjectPublicKeyInfo format and raw RSA format (from iOS SecKeyCopyExternalRepresentation).
     */
    public PublicKey parse(String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // First, try X.509 SubjectPublicKeyInfo format (standard Java format)
            try {
                X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytes);
                PublicKey key = keyFactory.generatePublic(x509Spec);
                logger.debug("Successfully parsed X.509 format RSA public key");
                return key;
            } catch (Exception x509Exception) {
                // If X.509 fails, try raw RSA format (from iOS SecKeyCopyExternalRepresentation)
                logger.debug("X.509 parsing failed, trying raw RSA format");
                return parseRawRSAPublicKey(keyBytes, keyFactory);
            }

        } catch (Exception e) {
            logger.error("Failed to parse RSA public key: {}", e.getMessage());
            throw new EncryptionException("Failed to parse RSA public key: " + e.getMessage(), e);
        }
    }

    /**
     * Parses a raw RSA public key using BouncyCastle ASN.1 parser.
     * This handles the format from iOS SecKeyCopyExternalRepresentation.
     */
    private PublicKey parseRawRSAPublicKey(byte[] keyBytes, KeyFactory keyFactory) {
        try {
            // Use BouncyCastle to parse the ASN.1 SEQUENCE
            ASN1Sequence sequence = ASN1Sequence.getInstance(keyBytes);
            
            // Extract modulus and exponent from the sequence
            // RSA public key format: SEQUENCE { modulus INTEGER, exponent INTEGER }
            ASN1Integer modulus = (ASN1Integer) sequence.getObjectAt(0);
            ASN1Integer exponent = (ASN1Integer) sequence.getObjectAt(1);
            
            // Create RSA public key spec
            RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(
                modulus.getValue(),
                exponent.getValue()
            );
            
            PublicKey key = keyFactory.generatePublic(rsaSpec);
            logger.debug("Successfully parsed raw RSA public key (iOS format)");
            return key;
            
        } catch (Exception e) {
            logger.error("Failed to parse raw RSA public key: {}", e.getMessage());
            throw new EncryptionException("Failed to parse raw RSA public key: " + e.getMessage(), e);
        }
    }
}
