# Cheqi Java SDK

Java SDK for issuing end-to-end encrypted Cheqi receipts, sending email fallback receipts, handling encrypted credit notes, and managing stores.

## Requirements

- Java 11+
- Maven 3.6+ or Gradle
- A Cheqi API key or an OAuth access token with the required scopes

## Installation

Maven:

```xml
<dependency>
    <groupId>io.cheqi</groupId>
    <artifactId>cheqi-sdk</artifactId>
    <version>2.0.0</version>
</dependency>
```

Gradle:

```gradle
implementation 'io.cheqi:cheqi-sdk:2.0.0'
```

## Environments

| Environment | URL | Use |
| --- | --- | --- |
| `Environment.SANDBOX` | `https://sandbox.api.cheqi.io` | Development and testing |
| `Environment.PRODUCTION` | `https://api.cheqi.io` | Live production |

Use `customApiEndpoint(String)` for local backend development.

## Initialize

With an API key:

```java
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .apiKey("sk_test_...")
    .build();
```

With per-call OAuth tokens:

```java
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .build();
```

When no API key is configured, pass an access token to receipt, matching, credit-note, or store methods that support it.

## Receipt Issuance Flow

`ReceiptService.processCompleteReceipt(...)` performs the current backend flow:

1. Resolves the recipient with `/recipient/resolve`.
2. Generates a receipt template with `/receipt/template`.
3. Encrypts one receipt envelope per matched recipient.
4. Submits the encrypted receipts with `/receipt/encrypted`.
5. If no Cheqi recipient is found but `recipientEmail` is present, sends a PDF fallback through `/receipt/email`.

## Issue a Receipt

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.models.Tax;
import com.cheqi.sdk.models.generated.BarcodeType;
import com.cheqi.sdk.models.generated.BuyerType;
import com.cheqi.sdk.models.generated.CardDetails;
import com.cheqi.sdk.models.generated.CardDetails.CardProviderEnum;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.NotificationDisplayCode;
import com.cheqi.sdk.models.generated.PaymentType;
import com.cheqi.sdk.models.generated.UnitCode;
import com.cheqi.sdk.receipt.ReceiptResult;

import java.math.BigDecimal;
import java.time.Instant;

CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .apiKey("sk_test_...")
    .build();

IdentificationDetails identificationDetails = new IdentificationDetails()
    .paymentType(PaymentType.CARD_PAYMENT)
    .cardDetails(new CardDetails()
        .paymentAccountReference("PAR123456789")
        .cardProvider(CardProviderEnum.VISA))
    .recipientEmail("customer@example.com");

ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
    .documentNumber("POS-2026-0001")
    .issueDate(Instant.now())
    .currency("EUR")
    .receiptSubtotal(new BigDecimal("10.00"))
    .totalBeforeTax(new BigDecimal("10.00"))
    .totalTaxAmount(new BigDecimal("2.10"))
    .totalAmount(new BigDecimal("12.10"))
    .addProduct(Product.builder()
        .name("Coffee beans")
        .brandName("Cheqi Coffee")
        .identifier("SKU-COFFEE-001")
        .quantity(1.0)
        .baseQuantity(1.0)
        .unitCode(UnitCode.C62)
        .unitPrice("10.00")
        .subtotal("10.00")
        .total("12.10")
        .addTax(21.0, "VAT", "10.00", "2.10")
        .build())
    .addTax(Tax.builder()
        .rate(21.0)
        .type("VAT")
        .taxableAmount("10.00")
        .amount("2.10")
        .label("VAT 21%")
        .build())
    .build();

ReceiptResult result = sdk.getReceiptService()
    .processCompleteReceipt(identificationDetails, receipt);

if (result.isSuccess()) {
    System.out.println("Delivery status: " + result.getDeliveryStatus());
    System.out.println("Cheqi receipt ID: " + result.getCheqiReceiptId());
} else if (result.isCustomerNotFound()) {
    System.out.println("No Cheqi recipient was found and no email fallback was available.");
} else {
    System.out.println("Receipt failed: " + result.getMessage());
}
```

With an OAuth token instead of an API key:

```java
ReceiptResult result = sdk.getReceiptService()
    .processCompleteReceipt(identificationDetails, receipt, accessToken);
```

## VAT Metadata

The backend requires VAT context on template generation:

- `buyerCountryCode`
- `buyerType`
- `taxesApplied`

The high-level receipt flow fills `buyerCountryCode` and `buyerType` from recipient resolution when available. You can override them on the SDK request:

```java
ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
    .documentNumber("POS-2026-0002")
    .issueDate(Instant.now())
    .currency("EUR")
    .receiptSubtotal(new BigDecimal("10.00"))
    .totalBeforeTax(new BigDecimal("10.00"))
    .totalTaxAmount(new BigDecimal("2.10"))
    .totalAmount(new BigDecimal("12.10"))
    .buyerCountryCode("NL")
    .buyerType(BuyerType.CONSUMER)
    .taxesApplied(true)
    .addProduct(Product.builder()
        .name("Coffee beans")
        .brandName("Cheqi Coffee")
        .identifier("SKU-COFFEE-002")
        .quantity(1.0)
        .baseQuantity(1.0)
        .unitCode(UnitCode.C62)
        .unitPrice("10.00")
        .subtotal("10.00")
        .total("12.10")
        .addTax(21.0, "VAT", "10.00", "2.10")
        .build())
    .addTax(21.0, "VAT", "10.00", "2.10")
    .build();
```

If `taxesApplied` is omitted, the SDK infers it as `true` when `totalTaxAmount` is positive or receipt-level taxes are present, otherwise `false`.

## Notification Display Code

For merchants with notification-code rendering enabled, attach a QR code or barcode to the high-level receipt flow. The SDK sends the metadata with every encrypted receipt submitted for that transaction.

```java
NotificationDisplayCode displayCode = new NotificationDisplayCode()
    .type(BarcodeType.QR_CODE)
    .data("https://example.com/receipt/POS-2026-0001");

ReceiptResult result = sdk.getReceiptService()
    .processCompleteReceipt(identificationDetails, receipt, accessToken, displayCode);
```

For `CODE_128`, the backend limits `data` to 32 characters. Use `QR_CODE` for longer values.

## Embedded Barcodes

Receipts can include embedded barcodes or QR codes at the receipt level or on individual product lines. Use these for return codes, loyalty references, tickets, serial numbers, product identifiers, or other scannable metadata.

Product-level barcode:

```java
Product product = Product.builder()
    .name("Coffee beans")
    .brandName("Cheqi Coffee")
    .identifier("SKU-COFFEE-001")
    .quantity(1.0)
    .baseQuantity(1.0)
    .unitCode(UnitCode.C62)
    .unitPrice("10.00")
    .subtotal("10.00")
    .total("12.10")
    .addTax(21.0, "VAT", "10.00", "2.10")
    .addBarcode(BarcodeType.EAN_13, "8712345678901", "EAN")
    .addQrCode("https://example.com/product/SKU-COFFEE-001", "Product QR")
    .build();
```

Receipt-level barcode:

```java
ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
    .documentNumber("POS-2026-0003")
    // other required receipt fields...
    .addProduct(product)
    .addBarcode(BarcodeType.QR_CODE, "https://example.com/return/POS-2026-0003", "Return code")
    .build();
```

## Email Fallback

Add `recipientEmail` to `IdentificationDetails` when you want fallback delivery for customers who are not using Cheqi yet.

```java
IdentificationDetails details = new IdentificationDetails()
    .paymentType(PaymentType.CARD_PAYMENT)
    .cardDetails(new CardDetails().paymentAccountReference("PAR123456789"))
    .recipientEmail("customer@example.com");
```

Behavior:

- Customer found: encrypted digital receipt is delivered to the Cheqi app or authorized recipient.
- Customer not found and `recipientEmail` provided: Cheqi sends a PDF receipt by email.
- Customer not found and no email: `ReceiptResult.isCustomerNotFound()` returns `true`.

## Lower-Level Receipt Methods

Use these when you want to control individual steps:

```java
RecipientResolutionResponse match = sdk.getMatchingService()
    .matchCustomer(identificationDetails, accessToken);

ReceiptTemplateResponse template = sdk.getReceiptService()
    .generateReceiptTemplate(receipt, List.of(ReceiptFormat.CHEQI), accessToken);

EncryptedReceiptRequest encrypted = sdk.getReceiptService()
    .createEncryptedReceipts(receiptEnvelopeJson, match.getRecipients().get(0));

ReceiptCreatedResponse created = sdk.getReceiptService()
    .sendEncryptedReceipts(Set.of(encrypted), templateHash, match, accessToken);
```

## Client-Encrypted Download Fallback

Set the generated `PaymentDetails` on `ReceiptPayload` for every issuance route:

```java
receiptPayload.paymentDetails(new PaymentDetails()
    .paymentMeansCode("48")
    .description("Card payment")
    .cardProvider("VISA")
    .cardLastFour("4242")
    .merchantId("MID-123")
    .paymentTerminalId("TID-456"));
```

`ReceiptPayload.paymentDetails` is definitive and is preserved unchanged. If it is absent,
payment means remain absent; matching data is not used as a fallback.

When `IdentificationDetails.paymentType` is present and recipient resolution selects
`DOWNLOAD_FALLBACK`, `issueReceipt` completes the fallback automatically. It serializes the
generated `ReceiptPayload`, inserts the same generated `IdentificationDetails` as a top-level
field, hashes that combined CHEQI JSON document, and places it in a generated
`ReceiptEnvelope`. The SDK then generates the fragment-key URL, encrypts the envelope with
AES-256-GCM, uploads only the download id, ciphertext, and hash, and returns the URL through
`ReceiptResult.getDownloadUrl()`.

This preserves any locally available payment identification without converting it into a
separate payment-means model. For a card payment that includes a PAR, card provider, or last
four digits, those values are therefore available to the encrypted download receipt. Cash can
be represented by `PaymentType.CASH` even when no other customer identification exists.

For an explicit **Customer without Cheqi** flow, call `issueDownloadReceipt` so the SDK skips
recipient matching and submits the locally encrypted download directly:

```java
ReceiptResult result = sdk.getReceiptService().issueDownloadReceipt(
    new IdentificationDetails().paymentType(PaymentType.CASH),
    receiptPayload,
    accessToken
);
```

If no payment type is available (for example, pairing-code-only matching), the result remains
`isDownloadEnvelopeRequired()` so the caller can provide another final generated
`ReceiptEnvelope` through `completeDownloadFallback`.

## Store Management

Store operations require an OAuth access token with the relevant store scopes.

```java
StoreDTO store = sdk.getStoreService()
    .createStore(companyId, createStoreRequest, accessToken);

List<StoreDTO> stores = sdk.getStoreService()
    .getStores(companyId, accessToken);

StoreDTO updated = sdk.getStoreService()
    .updateStore(companyId, storeId, updateStoreRequest, accessToken);
```

## Credit Notes

Credit notes use the same recipient-resolution and encryption model as receipts.

```java
CreditNoteResult result = sdk.getCreditNoteService()
    .processCompleteCreditNote(identificationDetails, creditNoteRequest, accessToken);
```

## Receipt Decryption and Merge

Recipients can decrypt queued receipt payloads and merge the backend-generated customer context into the receipt envelope:

```java
DecryptedReceipt decrypted = sdk.processEncryptedReceipt(encryptedReceipt, privateKey);

ReceiptEnvelope completeEnvelope = decrypted.getReceiptEnvelope();
```

The merge step injects receiving-party and payment-means context into supported Cheqi JSON and UBL formats.

## Verification

The SDK includes deterministic hashing helpers for receipt integrity checks:

```java
String hash = sdk.getVerificationService().calculateCheqiReceiptHash(cheqiReceipt);
```

## Error Handling

Most high-level SDK methods throw `CheqiSDKException`; lower-level HTTP calls throw `CheqiApiException`.

```java
try {
    ReceiptResult result = sdk.getReceiptService()
        .processCompleteReceipt(identificationDetails, receipt, accessToken);
} catch (CheqiSDKException e) {
    System.err.println(e.getMessage());
}
```

## Development

Run tests with Gradle:

```bash
./gradlew test
```

Build with Gradle:

```bash
./gradlew build
```

Build with Maven:

```bash
mvn -q -DskipTests compile
```

Regenerate OpenAPI models:

```bash
make generate
```
