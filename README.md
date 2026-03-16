<!--suppress ALL -->
# Cheqi Java SDK

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

The Cheqi Java SDK provides end-to-end encrypted receipt and credit note processing capabilities for suppliers, enabling secure digital receipt delivery to customers and handling returns/refunds through the Cheqi platform with bidirectional encrypted communication.

## Features

- 🔐 **End-to-End Encryption**: Hybrid RSA+AES encryption for secure receipt delivery
- 🎯 **Customer Matching**: Match customers using payment identifiers (cards, IBANs, etc.)
- 📄 **Receipt Generation**: Create UBL-compliant receipt templates from transaction data
- 💳 **Credit Note Processing**: Complete returns, refunds, and cancellations with encryption
- 🔄 **Bidirectional Flow**: Send receipts to customers, receive encrypted return requests
- 🏢 **Company Provisioning**: Provision companies and receive immediate API access
- 🏪 **Store Management**: Full CRUD operations for store/location management
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

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.*;
import com.cheqi.sdk.receipt.ProcessReceiptResult;
import com.cheqi.commons.enums.CardProvider;

// 1. Initialize the SDK
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .build();

// 2. Identify the customer (using card payment details)
IdentificationDetails customer = IdentificationDetails.builder()
    .paymentType(PaymentType.CARD_PAYMENT)
    .cardDetails(CardDetails.builder()
        .paymentAccountReference("PAR123456789")  // From payment terminal
        .cardProvider(CardProvider.VISA)
        .build())
    .customerEmail("customer@example.com")  // Fallback if you don't have a PAR (Payment Account Reference).
    .build();

// 3. Build the receipt
ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
    .documentNumber("INV-001")
    .currency("EUR")
    .totalAmount("12.10")
    .totalTaxAmount("2.10")
    .addProduct(Product.builder()
        .name("Coffee")
        .quantity(1.0)
        .unitCode(UnitCode.ONE)
        .unitPrice("10.00")
        .total("12.10")
        .addTax(21.0, "VAT", "2.10")
        .build())
    .build();

// 4. Send the receipt (one method does everything)
ProcessReceiptResult result = sdk.getReceiptService()
    .processCompleteReceipt(customer, receipt, accessToken);

if (result.isSuccess()) {
    System.out.println("Receipt sent!");
}

// Optional: include a notification QR/barcode for supported merchants
NotificationDisplayCode notificationDisplayCode = NotificationDisplayCode.builder()
    .type(BarcodeType.QR_CODE)
    .data("https://example.com/receipt/INV-001")
    .build();

ProcessReceiptResult notificationResult = sdk.getReceiptService()
    .processCompleteReceipt(customer, receipt, accessToken, notificationDisplayCode);
```

**Prerequisites:**
- **`companyId`** - Your company UUID (from provisioning or Cheqi dashboard)
- **`accessToken`** - OAuth token with receipt scopes (see [Authentication](#authentication))

## Environment Configuration

The SDK supports multiple environments:

| Environment              | URL                            | Description                 |
|--------------------------|--------------------------------|-----------------------------|
| `Environment.SANDBOX`    | `https://sandbox.api.cheqi.io` | Development                 |
| `Environment.TEST`       | `https://test.api.cheqi.io`    | Test environment            |
| `Environment.PRODUCTION` | `https://api.cheqi.io`         | Live production environment |


### SDK Initialization

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;

CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)  // Use SANDBOX for testing
    .build();
```

**Custom endpoint** (for local development):
```java
CheqiSDK sdk = CheqiSDK.builder()
    .customApiEndpoint("http://localhost:8080")
    .build();
```

### Processing Receipts

**One method handles everything** - customer matching, encryption, and delivery:

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

### Notification Display Code

If the merchant has notification-code rendering enabled, you can attach a QR code or barcode to the high-level receipt flow. The code is sent as request metadata and rendered by supported mobile clients directly from the push notification.

```java
NotificationDisplayCode notificationDisplayCode = NotificationDisplayCode.builder()
    .type(BarcodeType.CODE_128)
    .data("CHEQI-DEMO-123456")
    .build();

ProcessReceiptResult result = sdk.getReceiptService().processCompleteReceipt(
    identificationDetails,
    receiptRequest,
    accessToken,
    notificationDisplayCode
);
```

If the merchant is not enabled for this feature, or you omit `notificationDisplayCode`, the normal receipt push is sent.

### Email Fallback

**You can include an email addressDto in your initial request** for automatic fallback if the customer is not found:

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
    .apiKey("sk_live_...")
    .timeoutSeconds(60) // Custom timeout in seconds
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

### Convenience Methods for Products

For simple products, use the convenience `addProduct()` method that accepts all pre-calculated values:

```java
// Simple syntax - POS provides all calculated amounts
ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
    .documentNumber("INV-001")
    .currency("EUR")
    .totalAmount("302.50")
    .totalTaxAmount("52.50")
    
    // Add product with all amounts pre-calculated by POS
    .addProduct(
        "Nike",        // brand
        "AirMax",      // name
        "125.00",      // unitPrice
        "250.00",      // subtotal (125 × 2)
        21.0,          // taxRate
        "52.50",       // taxAmount (250 × 0.21)
        "302.50",      // total (250 + 52.50)
        2.0            // quantity
    )
    
    .addTax(Tax.builder()
        .rate(21.0)
        .type("VAT")
        .amount("52.50")
        .build())
    .build();
```

**Key Points:**
- ✅ **No calculations** - SDK just structures the data
- ✅ **POS responsibility** - All amounts must be pre-calculated
- ✅ **Type flexibility** - Accepts `String` or `BigDecimal` amounts
- ✅ **Still flexible** - Use full `Product.builder()` for complex cases (discounts, periods, custom SKUs)

**When to use convenience method:**
- Simple products with standard pricing
- Quick integration for basic POS systems
- When you already have all calculated values

**When to use Product.builder():**
- Products with discounts or charges
- Time-based products with periods (subscriptions, rentals)
- Products requiring detailed metadata (SKU, description, brand info)
- Complex tax structures

**BigDecimal overload:**
```java
.addProduct(
    "Nike",
    "AirMax",
    new BigDecimal("125.00"),  // unitPrice
    new BigDecimal("250.00"),  // subtotal
    21.0,                       // taxRate
    new BigDecimal("52.50"),   // taxAmount
    new BigDecimal("302.50"),  // total
    2.0                         // quantity
)
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
CreditNoteService creditNotes = sdk.getCreditNoteService();
CompanyService companies = sdk.getCompanyService();
StoreService stores = sdk.getStoreService();
CheqiApiClient apiClient = sdk.getApiClient();
```

### Company Provisioning

The `CompanyService` enables trusted POS systems to provision companies and receive immediate API access without waiting for merchant OAuth approval. Requires a ClientApplication with `partnerTier`.

```java
import com.cheqi.sdk.company.CompanyService;
import com.cheqi.sdk.models.company.Company;
import com.cheqi.sdk.models.company.Address;
import com.cheqi.sdk.models.company.ProvisionCompanyResponse;

// Build company details
Company company = Company.builder()
    .companyName("My Store BV")
    .companyLegalName("My Store B.V.")
    .chamberOfCommerceNumber("12345678")
    .companyEmail("info@mystore.nl")
    .addressDto(Address.builder()
        .streetName("Hoofdstraat 123")
        .cityName("Amsterdam")
        .postalZone("1012AB")
        .countryIsoCode("NL")
        .build())
    .build();

// Provision the company
ProvisionCompanyResponse response = sdk.getCompanyService()
    .provisionCompany(company, "owner@mystore.nl", clientApplicationToken);

if (response.isProvisioned()) {
    // New company created - use tokens immediately
    String accessToken = response.getAccessToken();
    UUID companyId = response.getCompanyId();
    System.out.println("Company provisioned: " + companyId);
} else if (response.isAlreadyExists()) {
    // Company exists - initiate OAuth flow for authorization
    UUID companyId = response.getCompanyId();
    System.out.println("Company exists, OAuth required: " + companyId);
}
```

**Provisioning Response States:**
- ✅ **`isProvisioned()`** - New company created, tokens returned for immediate use
- ⚠️ **`isAlreadyExists()`** - Company exists, OAuth flow required for authorization

### Store Management

The `StoreService` provides full CRUD operations for managing stores/locations. Requires `WRITE_STORES` scope.

```java
import com.cheqi.sdk.company.StoreService;
import com.cheqi.sdk.models.company.CreateStoreRequest;
import com.cheqi.sdk.models.company.Store;
import com.cheqi.sdk.models.company.Address;
import java.util.List;
import java.util.UUID;

// Create a new store
CreateStoreRequest storeRequest = CreateStoreRequest.builder()
    .storeName("My Store Amsterdam")
    .storeCode("STORE-001")
    .addressDto(Address.builder()
        .streetName("Kalverstraat 1")
        .cityName("Amsterdam")
        .postalZone("1012NX")
        .countryIsoCode("NL")
        .build())
    .phoneNumber("+31201234567")
    .email("amsterdam@mystore.nl")
    .openingHours("Mon-Sat: 09:00-18:00")
    .build();

Store store = sdk.getStoreService()
    .createStore(companyId, storeRequest, accessToken);
System.out.println("Store created: " + store.getId());

// List all stores for a company
List<Store> stores = sdk.getStoreService()
    .getStores(companyId, accessToken);

// Get only active stores
List<Store> activeStores = sdk.getStoreService()
    .getActiveStores(companyId, accessToken);

// Get a specific store
Store store = sdk.getStoreService()
    .getStore(companyId, storeId, accessToken);

// Update a store
CreateStoreRequest updateRequest = CreateStoreRequest.builder()
    .storeName("My Store Amsterdam - Central")
    .storeCode("STORE-001")
    .addressDto(Address.builder()
        .streetName("Kalverstraat 1")
        .cityName("Amsterdam")
        .postalZone("1012NX")
        .countryIsoCode("NL")
        .build())
    .build();

Store updatedStore = sdk.getStoreService()
    .updateStore(companyId, storeId, updateRequest, accessToken);

// Activate/Deactivate a store
sdk.getStoreService().activateStore(companyId, storeId, accessToken);
sdk.getStoreService().deactivateStore(companyId, storeId, accessToken);

// Delete a store
sdk.getStoreService().deleteStore(companyId, storeId, accessToken);
```

**Store Operations:**
- **`createStore()`** - Create a new store for a company
- **`getStores()`** - List all stores for a company
- **`getActiveStores()`** - List only active stores
- **`getStore()`** - Get a specific store by ID
- **`updateStore()`** - Update store details
- **`activateStore()`** / **`deactivateStore()`** - Toggle store status
- **`deleteStore()`** - Remove a store

**Address Fields:**
- **`streetName`** (required): Street name and number
- **`additionalStreetName`** (optional): Additional addressDto line
- **`addressLine`** (optional): Full addressDto line
- **`region`** (optional): State/province/region
- **`cityName`** (required): City name
- **`postalZone`** (required): Postal/ZIP code
- **`countryIsoCode`** (required): 2-letter ISO country code (e.g., "NL", "DE", "US")

### Credit Note Processing

The SDK supports complete credit note processing for returns, refunds, and cancellations with the same end-to-end encryption as receipts.

#### Processing Credit Notes

**One method handles everything** - customer identification via receipt ID, encryption, and delivery:

```java
import com.cheqi.sdk.creditNote.CreditNoteTemplateRequest;
import com.cheqi.sdk.creditNote.RefundPreference;
import com.cheqi.sdk.creditNote.RefundBankAccount;
import com.cheqi.sdk.creditNote.ReturnLineItem;
import com.cheqi.sdk.creditNote.ReturnCondition;
import com.cheqi.sdk.models.IdentificationDetails;
import com.cheqi.sdk.receipt.ProcessReceiptResult;
import java.time.Instant;
import java.util.UUID;

// 1. Identify customer via original receipt ID
// Credit notes use cheqiReceiptId to identify the customer (not payment details)
IdentificationDetails identificationDetails = IdentificationDetails.builder()
    .cheqiReceiptId("original-receipt-uuid")  // UUID from original receipt
    .build();

// 2. Build the credit note template
CreditNoteTemplateRequest creditNote = CreditNoteTemplateRequest.builder()
    .documentNumber("CN-2024-001")
    .issueDate(Instant.now())
    .currency("EUR")
    .originatorDocumentReference("original-receipt-number")  // Original invoice number
    .totalAmount("121.00")
    .totalTaxAmount("21.00")
    
    // Return items
    .addReturnLineItem(ReturnLineItem.builder()
        .name("Defective Laptop")
        .quantity(1.0)
        .unitPrice("100.00")
        .total("121.00")
        .taxRate(21.0)
        .taxAmount("21.00")
        .returnCondition(ReturnCondition.DEFECTIVE)
        .returnReason("Screen not working")
        .build())
    
    // Refund preference
    .refundPreference(RefundPreference.BANK_ACCOUNT)
    .refundBankAccount(RefundBankAccount.builder()
        .iban("NL91ABNA0417164300")
        .accountHolderName("John Doe")
        .build())
    
    .note("Refund for defective product")
    .build();

// 3. Process credit note (match customer + generate + encrypt + send)
ProcessReceiptResult result = sdk.getCreditNoteService()
    .processCompleteCreditNote(identificationDetails, creditNote, accessToken);

if (result.isSuccess()) {
    System.out.println("Credit note delivered successfully!");
    System.out.println("Credit notes created: " + result.getReceiptCount());
} else if (result.isCustomerNotFound()) {
    System.out.println("Customer not found");
} else {
    System.out.println("Processing failed: " + result.getMessage());
}
```

**This method automatically:**
1. **Identifies the customer** using the `cheqiReceiptId` from the original receipt
2. **Generates a credit note template** from the request data
3. **Creates encrypted credit notes** for all customer devices
4. **Sends the credit notes** to the Cheqi backend for delivery

#### Credit Note Fields

**Required Fields:**
- **`documentNumber`** - Credit note number (e.g., "CN-2024-001")
- **`issueDate`** - When the credit note was issued
- **`currency`** - ISO 4217 currency code (e.g., "EUR")
- **`cheqiReceiptId`** - UUID of the original receipt being credited
- **`totalAmount`** - Total credit amount (including tax)
- **`totalTaxAmount`** - Total tax amount

**Return Items:**
```java
.addReturnLineItem(ReturnLineItem.builder()
    .name("Product Name")
    .quantity(1.0)
    .unitPrice("100.00")
    .total("121.00")
    .taxRate(21.0)
    .taxAmount("21.00")
    .returnCondition(ReturnCondition.DEFECTIVE)  // or UNWANTED, DAMAGED
    .returnReason("Detailed reason for return")
    .build())
```

**Refund Options:**
```java
// Bank account refund
.refundPreference(RefundPreference.BANK_ACCOUNT)
.refundBankAccount(RefundBankAccount.builder()
    .iban("NL91ABNA0417164300")
    .accountHolderName("John Doe")
    .build())

// Or original payment method refund
.refundPreference(RefundPreference.ORIGINAL_PAYMENT_METHOD)
```

**Return Conditions:**
- **`DEFECTIVE`** - Product is defective or broken
- **`UNWANTED`** - Customer changed their mind
- **`DAMAGED`** - Product arrived damaged

#### Receiving Credit Note Requests (Merchant Side)

When a customer initiates a return through the Cheqi app, merchants receive an encrypted credit note request via webhook:

```java
import com.cheqi.commons.DTOs.EncryptedCreditNoteInitiationRequest;
import com.cheqi.commons.DTOs.CreditNoteInitiationRequest;

// Received from webhook
EncryptedCreditNoteInitiationRequest encryptedRequest = // ... from webhook

// Decrypt and parse the credit note request
CreditNoteInitiationRequest request = sdk.getCreditNoteService()
    .decryptCreditNoteRequest(encryptedRequest);

// Access the request details
System.out.println("Receipt ID: " + request.getCheqiReceiptId());
System.out.println("Return reason: " + request.getReturnReason());
System.out.println("Refund preference: " + request.getRefundPreference());

// Process the return based on your business logic
if (request.getRefundPreference() == RefundPreference.BANK_ACCOUNT) {
    RefundBankAccount bankAccount = request.getRefundBankAccount();
    System.out.println("Refund to IBAN: " + bankAccount.getIban());
    // Process bank refund...
} else {
    // Process refund to original payment method...
}
```

**Credit Note Request Flow:**
1. Customer initiates return in Cheqi app
2. Cheqi encrypts request with merchant's public key
3. Webhook delivers encrypted request to merchant
4. Merchant decrypts using SDK
5. Merchant processes return/refund
6. Merchant sends credit note back to customer (using `processCompleteCreditNote`)

#### Authentication Options

**Using API Key (Direct Merchant Integration):**
```java
// Configure SDK with API key
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.PRODUCTION)
    .apiKey("sk_live_...")
    .build();

// Process without access token - customer identified via cheqiReceiptId
ProcessReceiptResult result = sdk.getCreditNoteService()
    .processCompleteCreditNote(creditNote);
```

**Using OAuth2 Access Token (Third-Party Integration):**
```java
// Process with access token - customer identified via cheqiReceiptId
ProcessReceiptResult result = sdk.getCreditNoteService()
    .processCompleteCreditNote(creditNote, accessToken);
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
- **Forward Secrecy**: Unique AES keys for each receipt and credit note
- **Integrity Protection**: Built-in authentication tags
- **Multi-Device Support**: Encrypt once, deliver to multiple devices
- **Bidirectional Security**: Both receipts (merchant→customer) and credit note requests (customer→merchant) are encrypted end-to-end

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

### Complete Credit Note Example

Here's a complete example showing credit note processing for returns and refunds:

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.*;
import com.cheqi.sdk.creditNote.*;
import com.cheqi.sdk.receipt.ProcessReceiptResult;
import com.cheqi.commons.DTOs.EncryptedCreditNoteInitiationRequest;
import com.cheqi.commons.DTOs.CreditNoteInitiationRequest;
import com.cheqi.commons.enums.CardProvider;
import java.time.Instant;
import java.util.UUID;

public class CreditNoteExample {
    public static void main(String[] args) {
        // 1. Initialize SDK with private key for decryption
        CheqiSDK sdk = CheqiSDK.builder()
            .apiEndpoint(Environment.PRODUCTION)
            .apiKey("sk_live_...")
            .privateKey("your-base64-encoded-private-key")  // For decrypting customer requests
            .timeoutSeconds(30)
            .maxRetries(3)
            .build();
        
        // SCENARIO 1: Receiving a credit note request from customer
        // This happens when customer initiates a return in the Cheqi app
        EncryptedCreditNoteInitiationRequest encryptedRequest = // ... received from webhook
        
        try {
            // Decrypt the customer's return request
            CreditNoteInitiationRequest request = sdk.getCreditNoteService()
                .decryptCreditNoteRequest(encryptedRequest);
            
            System.out.println("📦 Return request received");
            System.out.println("Receipt ID: " + request.getCheqiReceiptId());
            System.out.println("Return reason: " + request.getReturnReason());
            System.out.println("Refund preference: " + request.getRefundPreference());
            
            // Process the return based on your business logic
            if (request.getRefundPreference() == RefundPreference.BANK_ACCOUNT) {
                RefundBankAccount bankAccount = request.getRefundBankAccount();
                System.out.println("💰 Refund to IBAN: " + bankAccount.getIban());
                // Process bank refund through your payment system...
            } else {
                System.out.println("💳 Refund to original payment method");
                // Process refund to original card/payment method...
            }
            
            // SCENARIO 2: Sending credit note back to customer after processing return
            // Build the credit note (customer identified via cheqiReceiptId)
            CreditNoteTemplateRequest creditNote = CreditNoteTemplateRequest.builder()
                .documentNumber("CN-2024-001")
                .issueDate(Instant.now())
                .currency("EUR")
                .cheqiReceiptId(request.getCheqiReceiptId())  // Identifies customer via original receipt
                .totalAmount("2902.79")
                .totalTaxAmount("503.79")
                
                // Return the laptop
                .addReturnLineItem(ReturnLineItem.builder()
                    .name("MacBook Pro 14\"")
                    .quantity(1.0)
                    .unitPrice("2499.00")
                    .total("2902.79")
                    .taxRate(21.0)
                    .taxAmount("503.79")
                    .returnCondition(ReturnCondition.DEFECTIVE)
                    .returnReason("Screen not working properly")
                    .build())
                
                // Refund details
                .refundPreference(request.getRefundPreference())
                .refundBankAccount(request.getRefundBankAccount())
                
                .note("Refund processed for defective product")
                .build();
            
            // Send encrypted credit note to customer (identified via cheqiReceiptId)
            ProcessReceiptResult result = sdk.getCreditNoteService()
                .processCompleteCreditNote(creditNote);
            
            if (result.isSuccess()) {
                System.out.println("✅ Credit note delivered successfully!");
                System.out.println("Credit notes created: " + result.getReceiptCount());
            } else {
                System.err.println("❌ Failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error processing credit note: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**Credit Note Flow Summary:**
1. **Customer initiates return** in Cheqi app → Encrypted request sent to merchant webhook
2. **Merchant decrypts request** using SDK → Processes return/refund in their system
3. **Merchant sends credit note** back to customer → Encrypted delivery to customer's devices
4. **Customer receives credit note** in Cheqi app → Complete audit trail of transaction

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
