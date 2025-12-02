<!--suppress ALL -->
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
    <version>1.0.2</version>
</dependency>
```

### Gradle

Add to your `build.gradle`:

```gradle
implementation 'io.cheqi:cheqi-sdk:1.0.2'
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
import com.cheqi.sdk.models.UnitCode;
import com.cheqi.sdk.models.Tax;
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
    .documentNumber("INV-001")  // Invoice/receipt number
    .issueDate(Instant.now())
    .currency("EUR")  // ISO 4217 currency code
    .invoiceSubtotal(new BigDecimal("100.00"))
    .totalBeforeTax(new BigDecimal("100.00"))
    .totalAmount(new BigDecimal("121.00"))
    .totalTaxAmount(new BigDecimal("21.00"))  // Total tax (POS calculated)
    .addProduct(Product.builder()
        .name("Laptop")
        .quantity(1.0)
        .unitCode(UnitCode.ONE)  // Type-safe unit codes
        .unitPrice("100.00")
        .subtotal("100.00")
        .total("121.00")
        .addTax(21.0, "VAT", "21.00")
        .build())
    .addTax(Tax.builder()  // Receipt-level tax breakdown
        .rate(21.0)
        .type("VAT")
        .taxableAmount("100.00")  // Amount before tax
        .amount("21.00")          // Tax amount (100 × 0.21)
        .label("VAT 21%")
        .build())
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

### Building Products

The SDK provides a fluent API for building product line items with type-safe unit codes:

```java
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.UnitCode;
import com.cheqi.sdk.models.Period;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Simple product
Product laptop = Product.builder()
    .name("MacBook Pro 14\"")
    .sku("MBP-14-2024")
    .quantity(1.0)
    .baseQuantity(1.0)        // Mandatory: 1.0 for simple items
    .unitCode(UnitCode.ONE)   // Type-safe enum
    .unitPrice("2499.00")
    .subtotal("2499.00")
    .total("3023.79")
    .addTax(21.0, "VAT", "524.79")
    .build();

// Pre-packaged product (10 packages × 500g cheese)
Product cheese = Product.builder()
    .name("Gouda Cheese")
    .sku("CHEESE-500G")
    .quantity(10.0)           // 10 packages
    .baseQuantity(500.0)      // 500g per package (total: 5000g)
    .unitCode(UnitCode.GRAM)
    .unitPrice("4.99")        // Price per package
    .subtotal("49.90")
    .total("60.38")
    .addTax(21.0, "VAT", "10.48")
    .build();

// Weight-based product
Product apples = Product.builder()
    .name("Organic Apples")
    .sku("APPLE-ORG")
    .quantity(2.5)
    .baseQuantity(1.0)        // Always 1.0 for weight-based
    .unitCode(UnitCode.KILOGRAM)
    .unitPrice("3.99")
    .addDiscount("1.00", "Loyalty discount")
    .addCharge("0.50", "Packaging fee")
    .subtotal("9.48")
    .total("11.47")
    .addTax(21.0, "VAT", "1.99")
    .build();

// Time-based product (subscription with period) - Easy with LocalDate!
Product subscription = Product.builder()
    .name("Premium Subscription")
    .sku("SUB-PREMIUM-M")
    .quantity(1.0)
    .baseQuantity(1.0)
    .unitCode(UnitCode.MONTH)
    .unitPrice("29.99")
    .subtotal("29.99")
    .total("36.29")
    .addTax(21.0, "VAT", "6.30")
    .period(Period.builder()      // Optional: for subscriptions, rentals
        .startDate(LocalDate.of(2024, 1, 1))      // Easy!
        .endDate(LocalDate.of(2024, 1, 31))       // Easy!
        .description("January 2024")
        .build())
    .build();

// Hourly rental with precise times - Easy with LocalDateTime!
Product carRental = Product.builder()
    .name("Tesla Model 3 Rental")
    .quantity(3.0)
    .baseQuantity(1.0)
    .unitCode(UnitCode.HOUR)
    .unitPrice("25.00")
    .subtotal("75.00")
    .total("90.75")
    .addTax(21.0, "VAT", "15.75")
    .period(Period.builder()
        .startDate(LocalDateTime.of(2024, 12, 1, 14, 0))  // 2:00 PM - Easy!
        .endDate(LocalDateTime.of(2024, 12, 1, 17, 0))    // 5:00 PM - Easy!
        .description("3-hour rental")
        .build())
    .build();
```

**Base Quantity Rules:**
- **Simple items**: Set `baseQuantity` to `1.0` (e.g., 1 laptop, 2.5kg apples)
- **Pre-packaged items**: Set `baseQuantity` to package size (e.g., 500 for 500g packages)
- **Total quantity**: `quantity × baseQuantity` (e.g., 10 × 500g = 5000g)

**Period Support:**
- Use `Period` for time-based products: subscriptions, rentals, utilities, service contracts
- Leave empty for one-time purchases
- **Easy to use**: Accepts `LocalDate` for date-only or `LocalDateTime` for precise times
- Automatically converts to `Instant` (ISO-8601 with timezone) internally
- Includes `startDate`, `endDate`, and optional `description`

**Period Helper Methods:**
```java
// Date-only (easiest for subscriptions, monthly billing)
.startDate(LocalDate.of(2024, 1, 1))
.endDate(LocalDate.of(2024, 1, 31))

// With specific times (for hourly rentals, events)
.startDate(LocalDateTime.of(2024, 12, 1, 14, 0))  // 2:00 PM
.endDate(LocalDateTime.of(2024, 12, 1, 17, 0))    // 5:00 PM

// With timezone (for international events)
.startDate(LocalDateTime.of(2024, 6, 15, 19, 0), ZoneId.of("Europe/Amsterdam"))
```

**Available Unit Codes:**
- **Common**: `ONE`, `EACH`, `SET`, `PAIR`, `DOZEN`
- **Weight**: `KILOGRAM`, `GRAM`, `POUND`, `OUNCE`
- **Volume**: `LITER`, `MILLILITER`, `GALLON_US`, `GALLON_UK`
- **Length**: `METER`, `CENTIMETER`, `INCH`, `FOOT`
- **Time**: `SECOND`, `MINUTE`, `HOUR`, `DAY`, `WEEK`, `MONTH`, `YEAR`
- **Packaging**: `BOX`, `CARTON`, `BOTTLE`, `CAN`, `BAG`, `PALLET`
- **Service**: `SERVICE_UNIT`, `ACTIVITY`, `JOB`

See `UnitCode` enum for the complete list of 70+ Peppol-compliant codes.

**Product Use Cases:**
- 🛒 **Simple items**: Laptops, books, clothing (baseQuantity = 1.0)
- 📦 **Pre-packaged**: Cheese packages, bottled water, egg cartons (baseQuantity = package size)
- ⚖️ **Weight-based**: Fruits, vegetables, bulk items (baseQuantity = 1.0)
- 📅 **Time-based**: Subscriptions, rentals, utilities (with Period)

### Tax Breakdown

The `Tax` model supports detailed tax reporting with taxable amounts:

```java
import com.cheqi.sdk.models.Tax;

// Single tax rate
Tax vat = Tax.builder()
    .rate(21.0)                    // 21% VAT
    .type("VAT")
    .taxableAmount("100.00")       // €100 taxable amount
    .amount("21.00")               // €21 tax (100 × 0.21)
    .label("VAT 21%")
    .build();

// Multiple tax rates on a receipt
ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
    .documentNumber("INV-001")
    .currency("EUR")
    .totalBeforeTax("10000.00")
    .totalTaxAmount("1865.00")
    .totalAmount("11865.00")
    
    // Tax breakdown by rate
    .addTax(Tax.builder()
        .rate(19.0)
        .type("VAT")
        .taxableAmount("8500.00")  // €8500 @ 19%
        .amount("1615.00")         // = €1615
        .label("VAT 19%")
        .build())
    
    .addTax(Tax.builder()
        .rate(9.0)
        .type("VAT")
        .taxableAmount("1500.00")  // €1500 @ 9% (reduced)
        .amount("135.00")          // = €135
        .label("VAT 9% (reduced)")
        .build())
    
    .build();
```

**Tax Fields:**
- **`rate`** (required): Tax percentage (e.g., 21.0 for 21%)
- **`type`** (required): Tax type (e.g., "VAT", "GST", "Sales Tax")
- **`taxableAmount`** (optional): Base amount before tax - useful for tax reporting
- **`amount`** (optional): Calculated tax amount
- **`label`** (optional): Display label (e.g., "VAT 21%")

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

## Complete Example

Here's a complete example showing the entire receipt processing flow:

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.*;
import com.cheqi.sdk.receipt.ProcessReceiptResult;
import com.cheqi.commons.enums.CardProvider;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ReceiptExample {
    public static void main(String[] args) {
        // 1. Initialize SDK
        CheqiSDK sdk = CheqiSDK.builder()
            .apiEndpoint(Environment.PRODUCTION)
            .timeout(30)
            .maxRetries(3)
            .build();
        
        // 2. Create customer identification
        IdentificationDetails customer = IdentificationDetails.builder()
            .paymentType(PaymentType.CARD_PAYMENT)
            .cardDetails(CardDetails.builder()
                .paymentAccountReference("PAR_1234567890")
                .cardProvider(CardProvider.VISA)
                .build())
            .customerEmail("customer@example.com")  // Optional fallback
            .build();
        
        // 3. Build products
        Product laptop = Product.builder()
            .name("MacBook Pro 14\"")
            .sku("MBP-14-2024")
            .quantity(1.0)
            .baseQuantity(1.0)        // Mandatory
            .unitCode(UnitCode.ONE)
            .unitPrice("2499.00")
            .addDiscount("100.00", "Black Friday")
            .subtotal("2399.00")
            .total("2902.79")
            .addTax(21.0, "VAT", "503.79")
            .build();
        
        Product apples = Product.builder()
            .name("Organic Apples")
            .sku("APPLE-ORG")
            .quantity(2.5)
            .baseQuantity(1.0)        // Mandatory
            .unitCode(UnitCode.KILOGRAM)
            .unitPrice("3.99")
            .subtotal("9.98")
            .total("12.08")
            .addTax(21.0, "VAT", "2.10")
            .build();
        
        // 4. Create receipt
        ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
            .documentNumber("INV-2024-001")
            .issueDate(Instant.now())
            .currency("EUR")
            .invoiceSubtotal(new BigDecimal("2408.98"))
            .totalBeforeTax(new BigDecimal("2408.98"))
            .totalAmount(new BigDecimal("2914.87"))
            .totalTaxAmount(new BigDecimal("505.89"))
            .addProduct(laptop)
            .addProduct(apples)
            .addTax(Tax.builder()
                .rate(21.0)
                .type("VAT")
                .taxableAmount("2408.98")  // Total taxable amount
                .amount("505.89")          // Total tax (2408.98 × 0.21)
                .label("VAT 21%")
                .build())
            .note("Thank you for your purchase!")
            .build();
        
        // 5. Process receipt (one method does everything)
        try {
            ProcessReceiptResult result = sdk.getReceiptService()
                .processCompleteReceipt(
                    customer,
                    receipt,
                    UUID.fromString("merchant-uuid"),
                    "access-token"
                );
            
            if (result.isSuccess()) {
                System.out.println("✅ Receipt delivered successfully!");
                System.out.println("Receipts created: " + result.getReceiptCount());
            } else if (result.isCustomerNotFound()) {
                System.out.println("⚠️ Customer not found - email receipt sent");
            } else {
                System.err.println("❌ Failed: " + result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## Migration Guide

If you're upgrading from an earlier version, here are the key changes:

### Breaking Changes

#### 1. Tax Structure Simplified

**Before (v1.0.0):**
```java
TaxBreakDown taxBreakdown = TaxBreakDown.builder()
    .taxTotal(new BigDecimal("21.00"))
    .addTaxSubTotal(TaxSubTotal.builder()
        .taxableAmount(new BigDecimal("100.00"))
        .taxAmount(new BigDecimal("21.00"))
        .taxCategoryId("S")
        .percent(21.0)
        .build())
    .build();

ReceiptTemplateRequest.builder()
    .taxBreakDown(taxBreakdown)
    .build();
```

**After (v1.1.0+):**
```java
ReceiptTemplateRequest.builder()
    .totalTaxAmount("21.00")  // Simple!
    .addTax(Tax.builder()
        .rate(21.0)
        .type("VAT")
        .taxableAmount("100.00")  // Optional: base amount
        .amount("21.00")          // Tax amount
        .build())
    .build();
```

#### 2. Field Renames

| Old Field | New Field | Reason |
|-----------|-----------|--------|
| `receiptId` | `documentNumber` | Clearer naming |
| `documentCurrencyCode` | `currency` | Simpler |

**Migration:**
```java
// Before
.receiptId("INV-001")
.documentCurrencyCode("EUR")

// After
.documentNumber("INV-001")
.currency("EUR")
```

#### 3. Unit Codes Now Type-Safe

**Before:**
```java
Product.builder()
    .unitCode("PCS")  // String - error-prone
    .build();
```

**After:**
```java
Product.builder()
    .unitCode(UnitCode.ONE)  // Enum - type-safe!
    .build();
```

**Note:** String codes are still accepted for JSON deserialization and will be automatically converted to the enum.

#### 4. Unit Code Now Mandatory

`unitCode` is now a required field for `Product`. This prevents validation issues and ensures Peppol compliance.

```java
// This will fail validation
Product.builder()
    .name("Product")
    .quantity(1.0)
    // .unitCode(UnitCode.ONE)  // ❌ Missing - will fail!
    .build();
```

### New Features

#### 1. Receipt-Level Discounts and Charges

```java
ReceiptTemplateRequest.builder()
    .addDiscount("10.00", "Loyalty discount")
    .addCharge("5.00", "Delivery fee")
    .build();
```

#### 2. Factory Methods for Discounts and Charges

```java
// Cleaner syntax
Discount discount = Discount.of("10.00", "Sale");
Charge charge = Charge.of("5.00", "Fee");

Product.builder()
    .addDiscount(discount)
    .addCharge(charge)
    .build();
```

#### 3. Period Support

```java
ReceiptTemplateRequest.builder()
    .period(Period.builder()
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2024, 1, 31))
        .description("January 2024 subscription")
        .build())
    .build();
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
