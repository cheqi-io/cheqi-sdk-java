package com.cheqi.sdk;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.DefaultCheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.receipt.ReceiptProcessor;
import com.cheqi.sdk.receipt.ReceiptService;
import com.cheqi.sdk.utils.RFC8785Canonicalizer;

/**
 * Main entry point for the Cheqi SDK providing end-to-end encrypted receipt processing.
 *
 * This SDK enables suppliers to:
 * 1. Match customers using payment identifiers
 * 2. Generate receipt templates from transaction data
 * 3. Create encrypted receipts for matched customers
 * 4. Communicate with Cheqi backend through encrypted channels
 * Example usage with predefined environment:
 * <pre>
 * CheqiSDK sdk = CheqiSDK.builder()
 *     .apiEndpoint(Environment.PRODUCTION)
 *     .supplierCredentials("client_id", "client_secret")
 *     .build();
 * </pre>
 *
 * Example usage with custom URL:
 * <pre>
 * CheqiSDK sdk = CheqiSDK.builder()
 *     .customApiEndpoint("https://custom.api.example.com")
 *     .supplierCredentials("client_id", "client_secret")
 *     .build();
 *
 * // Match customer using card details
 * RecipientResolutionRequest matchRequest = RecipientResolutionRequest.builder()
 *     .paymentType(PaymentType.CARD_PAYMENT)
 *     .card(CardDetails.builder()
 *         .paymentAccountReference("PAR123456")
 *         .cardProvider(CardProvider.VISA)
 *         .build())
 *     .customerEmail("customer@example.com")
 *     .build();
 *
 * RecipientResolutionResponse matchResponse = sdk.getMatchingService()
 *     .matchCustomer(matchRequest, accessToken);
 *
 * // Create receipt with simplified API
 * ReceiptTemplateRequest receiptRequest = ReceiptTemplateRequest.builder()
 *     .documentNumber("INV-001")
 *     .issueDate(Instant.now())
 *     .currency("EUR")
 *     .invoiceSubtotal(new BigDecimal("100.00"))
 *     .totalBeforeTax(new BigDecimal("100.00"))
 *     .totalAmount(new BigDecimal("121.00"))
 *     .totalTaxAmount(new BigDecimal("21.00"))
 *     .addProduct(Product.builder()
 *         .name("Laptop")
 *         .quantity(1.0)
 *         .unitCode(UnitCode.ONE)
 *         .unitPrice("100.00")
 *         .subtotal("100.00")
 *         .total("121.00")
 *         .addTax(21.0, "VAT", "21.00")
 *         .build())
 *     .addTax(Tax.builder()
 *         .rate(21.0)
 *         .type("VAT")
 *         .amount("21.00")
 *         .build())
 *     .build();
 *
 * // Process complete receipt (one method handles everything)
 * ProcessReceiptResult result = sdk.getReceiptService()
 *     .processCompleteReceipt(matchRequest, receiptRequest, merchantId, accessToken);
 *
 * if (result.isSuccess()) {
 *     System.out.println("Receipt delivered to " + result.getReceiptCount() + " devices");
 * }
 * </pre>
 */
public class CheqiSDK {

    private final CheqiSDKConfig config;
    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;
    private final CheqiApiClient apiClient;
    private final MatchingService matchingService;
    private final ReceiptService receiptService;
    private final ReceiptProcessor receiptProcessor;
    private final RFC8785Canonicalizer canonicalizer;

    private CheqiSDK(CheqiSDKConfig config) {
        this.config = config;
        this.canonicalizer = new RFC8785Canonicalizer();
        this.decryptionService = new DecryptionService();
        this.receiptProcessor = new ReceiptProcessor(decryptionService);
        this.encryptionService = new EncryptionService();
        this.apiClient = new DefaultCheqiApiClient(config);
        this.matchingService = new MatchingService(apiClient);
        this.receiptService = new ReceiptService(apiClient, encryptionService, matchingService);
    }

    /**
     * Creates a new SDK builder for configuration.
     */
    public static CheqiSDKBuilder builder() {
        return new CheqiSDKBuilder();
    }

    /**
     * Gets the encryption service for advanced operations.
     *
     * @return EncryptionService instance
     */
    public EncryptionService getEncryptionService() {
        return encryptionService;
    }

    /**
     * Gets the decryption service for advanced operations.
     *
     * @return DecryptionService instance
     */
    public DecryptionService getDecryptionService() {
        return decryptionService;
    }

    /**
     * Gets the matching service for customer identification.
     *
     * @return MatchingService instance
     */
    public MatchingService getMatchingService() {
        return matchingService;
    }

    // Expose receipt processing functionality
    public PurchaseReceipt processEncryptedReceipt(EncryptedReceiptDto encryptedReceipt, String privateKey) {
        return receiptProcessor.processEncryptedReceipt(encryptedReceipt, privateKey);
    }

    /**
     * Gets the receipt service for receipt template generation and encryption.
     *
     * @return ReceiptService instance
     */
    public ReceiptService getReceiptService() {
        return receiptService;
    }

    /**
     * Gets the API client for direct backend communication.
     *
     * @return CheqiApiClient instance
     */
    public CheqiApiClient getApiClient() {
        return apiClient;
    }

    /**
     * Gets the RFC 8785 canonicalizer for XML canonicalization.
     *
     * @return RFC8785Canonicalizer instance
     */
    public RFC8785Canonicalizer getCanonicalizer() {
        return canonicalizer;
    }

    /**
     * Gets the current SDK configuration.
     *
     * @return CheqiSDKConfig instance
     */
    public CheqiSDKConfig getConfig() {
        return config;
    }

    /**
     * Builder class for CheqiSDK configuration.
     */
    public static class CheqiSDKBuilder {
        private final CheqiSDKConfig.Builder configBuilder = CheqiSDKConfig.builder();

        /**
         * Sets the API endpoint using a predefined environment.
         *
         * @param apiEndpoint the predefined environment (SANDBOX, TEST, PRODUCTION, LOCAL)
         * @return this builder instance
         */
        public CheqiSDKBuilder apiEndpoint(Environment apiEndpoint) {
            configBuilder.apiEndpoint(apiEndpoint);
            return this;
        }

        /**
         * Sets a custom API endpoint URL.
         * Use this method when you need to connect to a custom or self-hosted Cheqi API instance.
         *
         * @param customUrl the custom API base URL (e.g., "https://custom.api.example.com")
         * @return this builder instance
         */
        public CheqiSDKBuilder customApiEndpoint(String customUrl) {
            configBuilder.customApiEndpoint(customUrl);
            return this;
        }

        public CheqiSDKBuilder supplierCredentials(String clientId, String clientSecret) {
            configBuilder.supplierCredentials(clientId, clientSecret);
            return this;
        }

        public CheqiSDKBuilder timeoutSeconds(int timeoutSeconds) {
            configBuilder.timeoutSeconds(timeoutSeconds);
            return this;
        }

        public CheqiSDKBuilder maxRetries(int maxRetries) {
            configBuilder.maxRetries(maxRetries);
            return this;
        }

        public CheqiSDK build() {
            return new CheqiSDK(configBuilder.build());
        }
    }
}