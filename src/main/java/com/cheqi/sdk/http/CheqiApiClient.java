package com.cheqi.sdk.http;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.DTOs.RecipientResolutionResponse;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.IdentificationDetails;
import com.cheqi.sdk.models.ReceiptTemplateRequest;

import java.util.Set;

/**
 * Interface for communicating with Cheqi backend APIs.
 *
 * Provides methods for:
 * - OAuth2 authentication
 * - Template receipt generation
 * - Customer matching
 *
 * This interface allows for multiple implementations (HTTP, gRPC, mock, etc.)
 * and makes testing easier through dependency injection.
 */
public interface CheqiApiClient {
    /**
     * Calls the template endpoint to generate a receipt template without personal data.
     *
     * The backend will:
     * 1. Validate the provided access token
     * 2. Process the business data (products, amounts, company info)
     * 3. Generate a UBL-compliant PurchaseReceipt template
     * 4. Insert placeholders for personal customer data in relevant fields
     * 5. Return the PurchaseReceipt template for personal data injection
     *
     * @param request ReceiptTemplateRequestDto containing business data
     * @param accessToken Pre-provided access token from the merchant
     * @return PurchaseReceipt with placeholders for personal data injection
     * @throws CheqiApiException if the API call fails
     */
    String generateReceiptTemplate(ReceiptTemplateRequest request, String accessToken) throws CheqiApiException;

    /**
     * Calls the customer matching endpoint to find a customer using payment identifiers.
     *
     * The backend will:
     * 1. Validate the provided access token and required permissions
     * 2. Process the payment identifiers (card details, payment account details, email)
     * 3. Attempt to match against registered customers using the provided identifiers
     * 4. Return customer device public keys for encryption if found
     * 5. Provide anonymous identifier and expiration time for privacy-preserving operations
     *
     * The matching process is privacy-focused:
     * - No personal customer data is returned to the merchant
     * - Only device public keys and anonymous identifiers are provided
     * - Enables end-to-end encryption where only the customer can decrypt receipts
     * - Third-parties can provide any combination of available identifiers
     *
     * @param request RecipientResolutionRequest containing available payment identifiers.
     *                Must include at least one of: card details, payment account details, or email.
     *                Third-parties can provide any combination of identifiers they have available.
     * @param accessToken Pre-provided access token from the merchant with 'write_receipts' scope
     * @return RecipientResolutionResponse with device public keys and anonymous identifier if customer found,
     *         or empty response with customerFound=false if no match
     * @throws CheqiApiException if the API call fails due to:
     *         - Invalid or missing access token (401)
     *         - Insufficient permissions (403)
     *         - Invalid request format (400)
     *         - Network connectivity issues
     *         - Backend server errors (5xx)
     */
    RecipientResolutionResponse matchCustomer(IdentificationDetails request, String accessToken) throws CheqiApiException;

    /**
     * Sends encrypted receipts to the backend for delivery to customer devices.
     *
     * This is the final step in the complete receipt delivery flow. The backend will:
     * 1. Validate the provided access token and required 'write_receipts' scope
     * 2. Store the encrypted receipts for each customer device
     * 3. Queue the receipts for delivery to customer devices via push notifications
     * 4. Return 201 Created status for successful receipt storage and queuing
     *
     * The complete receipt delivery process involves:
     * 1. Match customer using payment identifiers ({@link #matchCustomer})
     * 2. Generate receipt template from transaction data ({@link #generateReceiptTemplate})
     * 3. Encrypt receipt for customer devices (handled by EncryptionService)
     * 4. Send encrypted receipts to backend (this method)
     *
     * Privacy and Security:
     * - Receipts are end-to-end encrypted using customer device public keys
     * - Only the customer's devices can decrypt the receipt content
     * - The backend acts as a secure delivery mechanism without accessing receipt data
     * - Customer identity is protected through anonymous identifiers
     *
     * @param encryptedReceipts Set of encrypted receipts, one per customer device.
     *                         Each receipt contains device-specific encrypted data that can only
     *                         be decrypted by the corresponding device's private key.
     * @param accessToken Pre-provided OAuth2 access token from the merchant.
     *                   Must have 'write_receipts' scope for receipt submission permissions.
     * @throws CheqiApiException if the API call fails due to:
     *         <ul>
     *         <li><strong>401 Unauthorized</strong>: Invalid or expired access token</li>
     *         <li><strong>403 Forbidden</strong>: Access token missing required 'write_receipts' scope</li>
     *         <li><strong>400 Bad Request</strong>: Invalid request format or missing required fields</li>
     *         <li><strong>404 Not Found</strong>: Customer not found or anonymous identifier expired</li>
     *         <li><strong>429 Too Many Requests</strong>: Rate limit exceeded</li>
     *         <li><strong>5xx Server Errors</strong>: Backend server errors or temporary unavailability</li>
     *         <li><strong>Network Errors</strong>: Connection timeouts or network connectivity issues</li>
     *         </ul>
     * @see com.cheqi.sdk.encryption.EncryptionService for receipt encryption functionality
     * @see com.cheqi.sdk.receipt.ReceiptService#sendEncryptedReceipts for higher-level service method
     *
     * @since 1.0
     */
    void sendEncryptedReceipts(Set<EncryptedReceiptDto> encryptedReceipts, String templateHash, String accessToken) throws CheqiApiException;

    /**
     * Sends a receipt directly to a customer via email.
     *
     * This method provides a simpler alternative to the encrypted receipt flow when:
     * - The customer is not registered in the Cheqi system
     * - Email delivery is preferred over app-based delivery
     * - No end-to-end encryption is required
     *
     * The backend will:
     * 1. Validate the provided access token and required 'write_receipts' scope
     * 2. Process the receipt data
     * 3. Send the receipt to the specified email address
     * 4. Return 201 Created status for successful email delivery
     *
     * @param customerEmail Email address to send the receipt to. Must be a valid email format.
     * @param purchaseReceipt The PurchaseReceipt object containing the receipt data to send.
     * @param accessToken Pre-provided OAuth2 access token from the merchant.
     *                   Must have 'write_receipts' scope for receipt submission permissions.
     * @throws CheqiApiException if the API call fails due to:
     *         <ul>
     *         <li><strong>400 Bad Request</strong>: Invalid email format or receipt data</li>
     *         <li><strong>401 Unauthorized</strong>: Invalid or expired access token</li>
     *         <li><strong>403 Forbidden</strong>: Access token missing required 'write_receipts' scope</li>
     *         <li><strong>429 Too Many Requests</strong>: Rate limit exceeded</li>
     *         <li><strong>5xx Server Errors</strong>: Backend server errors or email delivery failure</li>
     *         <li><strong>Network Errors</strong>: Connection timeouts or network connectivity issues</li>
     *         </ul>
     *
     * @since 1.0
     */
    void sendReceiptViaEmail(String customerEmail, com.cheqi.commons.UBL.PurchaseReceipt purchaseReceipt, String accessToken) throws CheqiApiException;
}
