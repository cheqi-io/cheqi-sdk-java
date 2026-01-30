package com.cheqi.sdk.models;

public enum KeyAlgorithm {
    RSA_2048("RSA-2048", "RSA 2048-bit encryption with OAEP padding"),
    RSA_4096("RSA-4096", "RSA 4096-bit encryption with OAEP padding"),
    ECDSA_P256("ECDSA-P256", "Elliptic Curve P-256 (secp256r1)"),
    ECDSA_P384("ECDSA-P384", "Elliptic Curve P-384 (secp384r1)"),
    ED25519("Ed25519", "Edwards-curve Digital Signature Algorithm");

    private final String value;
    private final String description;

    KeyAlgorithm(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
