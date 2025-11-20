package com.cheqi.sdk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for cryptographic hashing operations.
 * 
 * Provides static methods for generating SHA-256 hashes in hexadecimal format.
 * All methods are thread-safe and optimized for performance.
 */
public final class HashUtils {
    private static final Logger logger = LoggerFactory.getLogger(HashUtils.class);
    
    // Private constructor to prevent instantiation
    private HashUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Generates a SHA-256 hash of the input string.
     * 
     * @param input String to hash
     * @return Lowercase hexadecimal representation of the SHA-256 hash
     * @throws IllegalArgumentException if input is null
     * @throws RuntimeException if SHA-256 algorithm is not available (should never happen)
     */
    public static String sha256Hex(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hash = bytesToHex(hashBytes);
            logger.debug("SHA-256 hash generated: {}...", hash.substring(0, Math.min(8, hash.length())));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Generates a SHA-256 hash of the input byte array.
     * 
     * @param input Byte array to hash
     * @return Lowercase hexadecimal representation of the SHA-256 hash
     * @throws IllegalArgumentException if input is null
     * @throws RuntimeException if SHA-256 algorithm is not available
     */
    public static String sha256Hex(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input byte array cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input);
            String hash = bytesToHex(hashBytes);
            logger.debug("SHA-256 hash generated for {} bytes", input.length);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Converts a byte array to a lowercase hexadecimal string.
     * 
     * @param bytes Byte array to convert
     * @return Lowercase hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
