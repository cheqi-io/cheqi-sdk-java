package com.cheqi.sdk.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.cheqi.sdk.utils.PemUtils;

/**
 * Parses RSA public keys from various formats.
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
            byte[] keyBytes = PemUtils.decodeKey(publicKeyBase64);
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
     * Parses a raw RSA public key using pure Java ASN.1 parsing.
     * This handles the format from iOS SecKeyCopyExternalRepresentation.
     */
    private PublicKey parseRawRSAPublicKey(byte[] keyBytes, KeyFactory keyFactory) {
        try {
            // Parse ASN.1 SEQUENCE manually
            // RSA public key format: SEQUENCE { modulus INTEGER, exponent INTEGER }
            int offset = 0;
            
            // Check SEQUENCE tag (0x30)
            if (keyBytes[offset++] != 0x30) {
                throw new IllegalArgumentException("Invalid ASN.1 SEQUENCE tag");
            }
            
            // Skip sequence length
            offset += getLengthBytes(keyBytes, offset);
            
            // Parse modulus INTEGER
            java.math.BigInteger modulus = parseASN1Integer(keyBytes, offset);
            offset += getIntegerSize(keyBytes, offset);
            
            // Parse exponent INTEGER
            java.math.BigInteger exponent = parseASN1Integer(keyBytes, offset);
            
            // Create RSA public key spec
            RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(modulus, exponent);
            
            PublicKey key = keyFactory.generatePublic(rsaSpec);
            logger.debug("Successfully parsed raw RSA public key (iOS format)");
            return key;
            
        } catch (Exception e) {
            logger.error("Failed to parse raw RSA public key: {}", e.getMessage());
            throw new EncryptionException("Failed to parse raw RSA public key: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses an ASN.1 INTEGER from the byte array at the given offset.
     */
    private java.math.BigInteger parseASN1Integer(byte[] data, int offset) {
        // Check INTEGER tag (0x02)
        if (data[offset++] != 0x02) {
            throw new IllegalArgumentException("Invalid ASN.1 INTEGER tag");
        }
        
        // Get length
        int length = getLength(data, offset);
        offset += getLengthBytes(data, offset);
        
        // Extract integer bytes
        byte[] intBytes = new byte[length];
        System.arraycopy(data, offset, intBytes, 0, length);
        
        return new java.math.BigInteger(intBytes);
    }
    
    /**
     * Gets the length value from ASN.1 length encoding.
     */
    private int getLength(byte[] data, int offset) {
        int firstByte = data[offset] & 0xFF;
        
        // Short form (length < 128)
        if ((firstByte & 0x80) == 0) {
            return firstByte;
        }
        
        // Long form
        int numBytes = firstByte & 0x7F;
        int length = 0;
        for (int i = 0; i < numBytes; i++) {
            length = (length << 8) | (data[offset + 1 + i] & 0xFF);
        }
        return length;
    }
    
    /**
     * Gets the number of bytes used for length encoding.
     */
    private int getLengthBytes(byte[] data, int offset) {
        int firstByte = data[offset] & 0xFF;
        
        // Short form (length < 128)
        if ((firstByte & 0x80) == 0) {
            return 1;
        }
        
        // Long form: 1 byte for the length-of-length + the actual length bytes
        return 1 + (firstByte & 0x7F);
    }
    
    /**
     * Gets the total size of an ASN.1 INTEGER including tag and length.
     */
    private int getIntegerSize(byte[] data, int offset) {
        int size = 1; // Tag byte
        int lengthBytes = getLengthBytes(data, offset + 1);
        int length = getLength(data, offset + 1);
        return size + lengthBytes + length;
    }
}
