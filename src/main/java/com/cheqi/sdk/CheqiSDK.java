package com.cheqi.sdk;

import com.cheqi.sdk.company.CompanyService;
import com.cheqi.sdk.company.StoreService;
import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.creditNote.CreditNoteService;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.DefaultCheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.models.EncryptedReceipt;
import com.cheqi.sdk.receipt.ReceiptProcessor;
import com.cheqi.sdk.receipt.ReceiptService;
import com.cheqi.sdk.verification.VerificationService;
import okhttp3.OkHttpClient;

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
 *     .apiKey("sk_live_...")
 *     .build();
 * </pre>
 *
 * Example usage with custom URL:
 * <pre>
 * CheqiSDK sdk = CheqiSDK.builder()
 *     .customApiEndpoint("https://custom.api.example.com")
 *     .apiKey("sk_test_...")
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
 *     .totalTaxAmount(new BigDecimal("21.00"))creditNoteTemplateRequest
 *     .addProduct(Product.builder()
 *         .name("Laptop")
 *         .quantity(1.0)
 *         .unitCode(UnitCode.ONE)
 *         .unitPrice("100.00")
 *         .subtotal("100.00")
 *         .total("121.00")
 *         .addTax(21.0, "VAT", "100.00", "21.00")
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
    private final CompanyService companyService;
    private final StoreService storeService;
    private final VerificationService verificationService;
    private final CreditNoteService creditNoteService;


    private CheqiSDK(CheqiSDKConfig config) {
        this.config = config;
        this.verificationService = new VerificationService();
        this.decryptionService = new DecryptionService();
        this.receiptProcessor = new ReceiptProcessor(decryptionService);
        this.encryptionService = new EncryptionService();
        this.apiClient = new DefaultCheqiApiClient(config);
        this.matchingService = new MatchingService(apiClient);
        this.receiptService = new ReceiptService(apiClient, encryptionService, matchingService);
        this.companyService = new CompanyService(apiClient);
        this.storeService = new StoreService(apiClient);
        this.creditNoteService = new CreditNoteService(apiClient, encryptionService, matchingService);
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
    public DecryptedReceipt processEncryptedReceipt(EncryptedReceipt encryptedReceipt, String privateKey) {
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
     * Gets the verification service for receipt integrity verification.
     * Provides canonicalization and hashing for both CheqiReceipt (JSON) and UBL XML formats.
     *
     * @return VerificationService instance
     */
    public VerificationService getVerificationService() {
        return verificationService;
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
     * Gets the company service.
     *
     * @return CompanyService instance
     */
    public CompanyService getCompanyService() {
        return companyService;
    }

    /**
     * Gets the store service for store management.
     *
     * @return StoreService instance
     */
    public StoreService getStoreService() {
        return storeService;
    }

    /**
     * Gets the credit note service for credit note processing.
     *
     * @return CreditNoteService instance
     * @throws IllegalStateException if no private key was configured during SDK initialization
     */
    public CreditNoteService getCreditNoteService() {
        return creditNoteService;
    }

    /**
     * Builder class for CheqiSDK configuration.
     */
    public static class CheqiSDKBuilder {
        private final CheqiSDKConfig.Builder configBuilder = CheqiSDKConfig.builder();

        /**
         * Sets the API endpoint using a predefined environment.
         *
         * @param apiEndpoint the predefined environment (SANDBOX or PRODUCTION)
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

        public CheqiSDKBuilder apiKey(String apiKey) {
            configBuilder.apiKey(apiKey);
            return this;
        }

        public CheqiSDKBuilder privateKey(String privateKey) {
            configBuilder.privateKey(privateKey);
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

        public CheqiSDKBuilder httpClient(OkHttpClient httpClient) {
            configBuilder.httpClient(httpClient);
            return this;
        }

        public CheqiSDK build() {
            return new CheqiSDK(configBuilder.build());
        }
    }
}
