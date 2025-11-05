package com.cheqi.sdk.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RFC 8785 compliant JSON canonicalization implementation.
 * Provides deterministic JSON serialization for consistent hashing.
 */
public class RFC8785Canonicalizer {

    private final ObjectMapper objectMapper;

    public RFC8785Canonicalizer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Canonicalizes JSON string according to RFC 8785
     */
    public String canonicalize(String json) throws IOException {
        JsonNode node = objectMapper.readTree(json);
        return canonicalizeNode(node);
    }

    /**
     * Canonicalizes an object directly by first converting to JSON
     */
    public String canonicalize(Object object) throws IOException {
        String json = objectMapper.writeValueAsString(object);
        return canonicalize(json);
    }

    /**
     * Calculates SHA-256 hash of canonicalized JSON
     */
    public String calculateHash(String json) throws IOException {
        String canonical = canonicalize(json);
        return calculateSHA256(canonical);
    }

    /**
     * Calculates SHA-256 hash of canonicalized object
     */
    public String calculateHash(Object object) throws IOException {
        String canonical = canonicalize(object);
        return calculateSHA256(canonical);
    }

    private String canonicalizeNode(JsonNode node) {
        if (node.isObject()) {
            return canonicalizeObject((ObjectNode) node);
        } else if (node.isArray()) {
            return canonicalizeArray((ArrayNode) node);
        } else if (node.isTextual()) {
            return canonicalizeString(node.asText());
        } else if (node.isNumber()) {
            return canonicalizeNumber(node);
        } else if (node.isBoolean()) {
            return node.asBoolean() ? "true" : "false";
        } else if (node.isNull()) {
            return "null";
        }
        throw new IllegalArgumentException("Unsupported JSON node type");
    }

    private String canonicalizeObject(ObjectNode objectNode) {
        StringBuilder sb = new StringBuilder("{");

        // Sort keys lexicographically (RFC 8785 requirement)
        List<String> sortedKeys = new ArrayList<>();
        objectNode.fieldNames().forEachRemaining(sortedKeys::add);
        Collections.sort(sortedKeys);

        boolean first = true;
        for (String key : sortedKeys) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append(canonicalizeString(key));
            sb.append(":");
            sb.append(canonicalizeNode(objectNode.get(key)));
        }

        sb.append("}");
        return sb.toString();
    }

    private String canonicalizeArray(ArrayNode arrayNode) {
        StringBuilder sb = new StringBuilder("[");

        boolean first = true;
        for (JsonNode element : arrayNode) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(canonicalizeNode(element));
        }

        sb.append("]");
        return sb.toString();
    }

    private String canonicalizeString(String str) {
        StringBuilder sb = new StringBuilder("\"");

        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }

        sb.append("\"");
        return sb.toString();
    }

    private String canonicalizeNumber(JsonNode numberNode) {
        if (numberNode.isIntegralNumber()) {
            return numberNode.asText();
        } else {
            // For floating point, use BigDecimal to avoid scientific notation
            // Respect the backend's original representation
            BigDecimal decimal = numberNode.decimalValue();
            return decimal.toPlainString();
        }
    }

    /**
     * Calculates SHA-256 hash of the input string
     */
    private String calculateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}