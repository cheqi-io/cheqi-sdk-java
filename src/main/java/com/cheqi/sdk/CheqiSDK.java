package com.cheqi.sdk;

import com.cheqi.sdk.company.CompanyService;
import com.cheqi.sdk.company.StoreService;
import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.creditNote.CreditNoteService;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.download.DownloadService;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.DefaultCheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.receipt.ReceiptService;
import com.cheqi.sdk.verification.VerificationService;
import okhttp3.OkHttpClient;

/**
 * Main entry point for the Cheqi SDK providing end-to-end encrypted receipt processing.
 *
 * This SDK enables issuers to resolve a recipient, encrypt the issuer-supplied definitive receipt
 * payload independently for every owner device, and submit the encrypted envelope to Cheqi.
 * Cheqi does not receive the plaintext receipt body, and this SDK does not calculate receipt values.
 *
 * <p>Example configuration:</p>
 * <pre>
 * CheqiSDK sdk = CheqiSDK.builder()
 *     .apiEndpoint(Environment.PRODUCTION)
 *     .apiKey("sk_live_...")
 *     .build();
 * </pre>
 *
 */
public class CheqiSDK {

    private final CheqiSDKConfig config;
    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;
    private final DownloadService downloadService;
    private final CheqiApiClient apiClient;
    private final MatchingService matchingService;
    private final ReceiptService receiptService;
    private final CompanyService companyService;
    private final StoreService storeService;
    private final VerificationService verificationService;
    private final CreditNoteService creditNoteService;


    private CheqiSDK(CheqiSDKConfig config) {
        this.config = config;
        this.verificationService = new VerificationService();
        this.decryptionService = new DecryptionService();
        this.downloadService = new DownloadService();
        this.encryptionService = new EncryptionService();
        this.apiClient = new DefaultCheqiApiClient(config);
        this.matchingService = new MatchingService(apiClient);
        this.receiptService = new ReceiptService(
                apiClient,
                encryptionService,
                matchingService,
                downloadService,
                config.getReceiptDownloadBaseUrl()
        );
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

    /** Gets stateless client-encrypted receipt download helpers. */
    public DownloadService getDownloadService() {
        return downloadService;
    }

    /**
     * Gets the matching service for customer identification.
     *
     * @return MatchingService instance
     */
    public MatchingService getMatchingService() {
        return matchingService;
    }

    /**
     * Gets the receipt service for definitive payload encryption and submission.
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

        /** Sets the customer-facing receipt origin for a custom API endpoint. */
        public CheqiSDKBuilder receiptDownloadBaseUrl(String receiptDownloadBaseUrl) {
            configBuilder.receiptDownloadBaseUrl(receiptDownloadBaseUrl);
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
