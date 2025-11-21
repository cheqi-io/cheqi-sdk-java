# Cheqi Java SDK

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

The Cheqi Java SDK provides end-to-end encrypted receipt processing capabilities for suppliers, enabling secure digital receipt delivery to customers through the Cheqi platform.

## Features

- 🔐 **End-to-End Encryption**: Hybrid RSA+AES encryption for secure receipt delivery
- 🎯 **Customer Matching**: Match customers using payment identifiers (cards, IBANs, etc.)
- 📄 **Receipt Generation**: Create UBL-compliant receipt templates from transaction data
- 🌐 **Multi-Environment Support**: Sandbox, Test, and Production environments
- 🔄 **Automatic Retry Logic**: Built-in retry mechanisms for network operations
- 📊 **RFC8785 Canonicalization**: Consistent JSON canonicalization for integrity verification
- 🏗️ **Builder Pattern**: Fluent API design for easy configuration

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.cheqi</groupId>
    <artifactId>cheqi-sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle

Add to your `build.gradle`:

```gradle
implementation 'io.cheqi:cheqi-sdk:1.0-SNAPSHOT'
```

## Quick Start


## Environment Configuration

The SDK supports multiple environments:

| Environment              | URL                            | Description                 |
|--------------------------|--------------------------------|-----------------------------|
| `Environment.SANDBOX`    | `https://sandbox.api.cheqi.io` | Development                 |
| `Environment.TEST`       | `https://test.api.cheqi.io`    | Test environment            |
| `Environment.PRODUCTION` | `https://api.cheqi.io`         | Live production environment |


### 1. Initialize the SDK

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;

// Initialize SDK with your credentials
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)  // Use SANDBOX for testing
    .timeout(30)  // Request timeout in seconds
    .maxRetries(3)  // Number of retry attempts
    .build();
```

**Environments:**
- `Environment.SANDBOX` - `https://sandbox.api.cheqi.io` (for development)
- `Environment.TEST` - `https://test.api.cheqi.io` (for testing)
- `Environment.PRODUCTION` - `https://api.cheqi.io` (for live transactions)
- `.customApiEndpoint("https://custom.url")` - For local development

### Step 4: Process a Receipt

**This is the simplest way to process receipts** - one method handles everything:

```java
import com.cheqi.sdk.models.IdentificationDetails;
import com.cheqi.sdk.models.CardDetails;
import com.cheqi.sdk.models.PaymentType;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.TaxBreakDown;
import com.cheqi.sdk.receipt.ProcessReceiptResult;
import com.cheqi.commons.enums.CardProvider;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Create identification details for customer matching
IdentificationDetails identificationDetails = IdentificationDetails.builder()
    .paymentType(PaymentType.CARD_PAYMENT)
    .cardDetails(CardDetails.builder()
        .paymentAccountReference("PAR123456")
        .cardProvider(CardProvider.VISA)
        .build())
    .build();

// Create receipt template request
ReceiptTemplateRequest receiptRequest = ReceiptTemplateRequest.builder()
    .receiptId("INV-001")
    .issueDate(Instant.now())
    .documentCurrencyCode("EUR")
    .invoiceSubtotal(new BigDecimal("100.00"))
    .totalBeforeTax(new BigDecimal("100.00"))
    .totalAmount(new BigDecimal("121.00"))
    .products(productList)  // List<Product>
    .taxBreakDown(taxBreakdown)  // TaxBreakDown
    .build();

// Process complete receipt (match + generate + encrypt + send)
ProcessReceiptResult result = sdk.getReceiptService().processCompleteReceipt(
    identificationDetails,  // Customer identification details
    receiptRequest,         // Receipt template data
    merchantId,             // UUID of the merchant
    accessToken             // OAuth access token
);

if (result.isSuccess()) {
    System.out.println("Receipt delivered successfully!");
    System.out.println("Receipts created: " + result.getReceiptCount());
} else if (result.isCustomerNotFound()) {
    // Customer not found - prompt for email and send request with email included
    System.out.println("Customer not found. Prompt user for email to send receipt.");
    // See 'Email Fallback' section below for how to include email
} else {
    System.out.println("Processing failed: " + result.getMessage());
}
```

**This method automatically:**
1. **Matches the customer** using the provided identification details (card PAR, IBAN, email, etc.)
2. **Generates a receipt template** from the request data (only if customer is found)
3. **Creates encrypted receipts** for all customer devices
4. **Sends the receipts** to the Cheqi backend for delivery

### Email Fallback

**You can include an email address in your initial request** for automatic fallback if the customer is not found:

```java
// Add email to identification details for fallback
IdentificationDetails identificationDetails = IdentificationDetails.builder()
    .paymentType(PaymentType.CARD_PAYMENT)
    .cardDetails(CardDetails.builder()
        .paymentAccountReference("PAR123456")
        .cardProvider(CardProvider.VISA)
        .build())
    .customerEmail("customer@example.com")  // Email fallback
    .build();

// Process receipt - email will be used if customer not found
ProcessReceiptResult result = sdk.getReceiptService().processCompleteReceipt(
    identificationDetails,
    receiptRequest,
    merchantId,
    accessToken
);

if (result.isSuccess()) {
    System.out.println("Receipt delivered successfully!");
} else if (result.isCustomerNotFound()) {
    System.out.println("Customer not found - please collect email for receipt delivery");
} else {
    System.out.println("Failed: " + result.getMessage());
}
```

**Email Fallback Behavior:**
- ✅ Customer **found** → Encrypted digital receipt delivered to their Cheqi app (email is ignored)
- ✅ Customer **not found** + **email provided** → PDF receipt sent via email (handled by Cheqi)
- ❌ Customer **not found** + **no email** → Returns `isCustomerNotFound() = true`

**Tip:** If you collect email at checkout, include it in the initial request to avoid a second API call.


## Advanced Usage

### Custom Configuration

```java
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.PRODUCTION)
    .supplierCredentials("client-id", "client-secret")
    .timeout(60)        // Custom timeout in seconds
    .maxRetries(5)      // Custom retry count
    .build();
```

### Direct Service Access

```java
// Access individual services for advanced operations
EncryptionService encryption = sdk.getEncryptionService();
DecryptionService decryption = sdk.getDecryptionService();
MatchingService matching = sdk.getMatchingService();
ReceiptService receipts = sdk.getReceiptService();
CheqiApiClient apiClient = sdk.getApiClient();
```

### Processing Encrypted Receipts

```java
import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.UBL.PurchaseReceipt;

// Decrypt and process a received encrypted receipt
EncryptedReceiptDto encryptedReceipt = // ... received from somewhere
String privateKeyPem = // ... your RSA private key

PurchaseReceipt receipt = sdk.processEncryptedReceipt(encryptedReceipt, privateKeyPem);
System.out.println("Receipt ID: " + receipt.getId().getValue());
System.out.println("Total: " + receipt.getLegalMonetaryTotal().getTaxInclusiveAmount().getValue());
```

### Canonical JSON Hashing

```java
import com.cheqi.sdk.utils.RFC8785Canonicalizer;

// Generate consistent hashes for receipt integrity
RFC8785Canonicalizer canonicalizer = new RFC8785Canonicalizer();
String canonicalJson = canonicalizer.canonicalize(receiptObject);
String hash = canonicalizer.canonicalizeAndHash(receiptObject);
```

## Error Handling

The SDK provides comprehensive error handling:

```java
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.exceptions.ReceiptProcessingException;
import com.cheqi.sdk.http.exceptions.CheqiApiException;

try {
    RecipientResolutionResponse response = sdk.getMatchingService()
        .matchCustomer(request, accessToken);
} catch (CheqiApiException e) {
    System.err.println("API Error: " + e.getMessage());
    System.err.println("Status Code: " + e.getStatusCode());
} catch (CheqiSDKException e) {
    System.err.println("SDK Error: " + e.getMessage());
} catch (Exception e) {
    System.err.println("Unexpected error: " + e.getMessage());
}
```

## Security Features

### Encryption

- **Hybrid Encryption**: RSA-OAEP for key exchange + AES-256-GCM for data
- **Forward Secrecy**: Unique AES keys for each receipt
- **Integrity Protection**: Built-in authentication tags
- **Multi-Device Support**: Encrypt once, deliver to multiple devices

### Key Management

- **RSA Key Pairs**: 2048-bit minimum key size
- **Secure Key Storage**: Integration with platform key stores
- **Key Rotation**: Support for key lifecycle management

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6+ (for building)
- **Dependencies**: All dependencies are managed automatically

## Dependencies

The SDK includes the following key dependencies:

- **Jackson**: JSON processing and serialization
- **OkHttp**: HTTP client for API communication  
- **SLF4J**: Logging framework
- **JAXB**: XML binding for UBL processing

## Building from Source

```bash
# Clone the repository
git clone https://github.com/cheqi/cheqi-sdk-java.git
cd cheqi-sdk-java

# Build the project
mvn clean compile

# Create JAR with dependencies
mvn package
```

## API Reference

For detailed API documentation, see the [JavaDoc](docs/javadoc/) or generate it locally:

```bash
mvn javadoc:javadoc
```

## Support

- **Documentation**: [https://docs.cheqi.io](https://docs.cheqi.io)
- **Support Email**: info@cheqi.io

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Cheqi** - Secure Digital Receipt Platform
