# Cheqi Java SDK

Java SDK for resolving receipt recipients and issuing end-to-end encrypted Cheqi receipts and credit notes. It also provides client-encrypted receipt downloads, receipt-envelope decryption, integrity helpers, and store management.

The SDK preserves Cheqi's zero-knowledge boundary: receipt contents are supplied by the issuer, encrypted locally for each owner device, and submitted to Cheqi as ciphertext. The SDK does not calculate, enrich, or plaintext-assemble receipt values.

## Requirements

- Java 11 or newer
- Maven 3.6 or newer, or Gradle
- A Cheqi API key, or an OAuth access token with the required permissions

## Installation

Version `2.0.0` describes the API on this branch but has not been published to Maven Central yet. Until it is released, build the SDK locally or use the latest published release where its API is sufficient.

After `2.0.0` is published, add it with Maven:

```xml
<dependency>
    <groupId>io.cheqi</groupId>
    <artifactId>cheqi-sdk</artifactId>
    <version>2.0.0</version>
</dependency>
```

Or with Gradle:

```gradle
implementation 'io.cheqi:cheqi-sdk:2.0.0'
```

To build and install this branch locally:

```bash
./gradlew publishToMavenLocal
```

## Environments

| Environment | URL | Use |
| --- | --- | --- |
| `Environment.SANDBOX` | `https://sandbox.api.cheqi.io` | Development and testing |
| `Environment.PRODUCTION` | `https://api.cheqi.io` | Live production |

Use `customApiEndpoint(String)` for local or custom backend environments. If that environment also uses a custom customer-facing receipt download origin, configure it with `receiptDownloadBaseUrl(String)`.

## Initialize the SDK

With an API key:

```java
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .apiKey("sk_test_...")
    .build();
```

With per-call OAuth access tokens:

```java
CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .build();
```

When no API key is configured, pass the OAuth access token to the service method that supports it. Do not put credentials or plaintext receipt contents in logs.

## Receipt Issuance

`ReceiptService.issueReceipt(...)` performs the current receipt flow:

1. Resolve a delivery route from the supplied `IdentificationDetails`.
2. For a digital route, serialize the definitive `ReceiptPayload` without changing its values.
3. Encrypt that payload independently for every matched owner device.
4. Submit an `EncryptedReceiptEnvelope` containing only ciphertext.
5. Return a `ReceiptResult` describing the accepted receipt or the required fallback action.

Cheqi knows the routing metadata needed for recipient matching. It does not receive the plaintext `ReceiptPayload` on the digital route.

### Issue a card receipt

```java
import com.cheqi.sdk.CheqiSDK;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.ReceiptPayload;
import com.cheqi.sdk.models.Tax;
import com.cheqi.sdk.models.generated.CardDetails;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.PaymentDetails;
import com.cheqi.sdk.models.generated.PaymentType;
import com.cheqi.sdk.models.generated.UnitCode;
import com.cheqi.sdk.receipt.ReceiptResult;

import java.time.OffsetDateTime;

CheqiSDK sdk = CheqiSDK.builder()
    .apiEndpoint(Environment.SANDBOX)
    .apiKey("sk_test_...")
    .build();

IdentificationDetails identificationDetails = new IdentificationDetails()
    .paymentType(PaymentType.CARD_PAYMENT)
    .cardDetails(new CardDetails()
        .paymentAccountReference("PAR123456789")
        .cardProvider(CardDetails.CardProviderEnum.VISA)
        .lastFourDigits("4242"));

ReceiptPayload receiptPayload = ReceiptPayload.builder()
    .documentNumber("POS-2026-0001")
    .issueDate(OffsetDateTime.now())
    .currency("EUR")
    .receiptSubtotal("10.00")
    .totalBeforeTax("10.00")
    .totalTaxAmount("2.10")
    .totalAmount("12.10")
    .taxesApplied(true)
    .paymentDetails(new PaymentDetails()
        .paymentMeansCode("48")
        .description("Card payment")
        .cardProvider("VISA")
        .cardLastFour("4242")
        .merchantId("MID-123")
        .paymentTerminalId("TID-456"))
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
    .issueReceipt(identificationDetails, receiptPayload);

if (result.isAccepted()) {
    System.out.println("Receipt accepted: " + result.getCheqiReceiptId());
    System.out.println("Delivery route: " + result.getDeliveryRouteType());
} else if (result.isEmailReceiptRequired()) {
    // Generate the permitted email-fallback receipt and submit it explicitly.
} else if (result.isDownloadEnvelopeRequired()) {
    // Generate the final ReceiptEnvelope locally, then call completeDownloadFallback(...).
}
```

With an OAuth access token:

```java
ReceiptResult result = sdk.getReceiptService()
    .issueReceipt(identificationDetails, receiptPayload, accessToken);
```

To associate the receipt with a store, use the overload accepting `storeId`:

```java
ReceiptResult result = sdk.getReceiptService()
    .issueReceipt(identificationDetails, receiptPayload, storeId, accessToken);
```

### Supplying definitive receipt values

`ReceiptPayload` is the issuer's definitive generation input. The SDK preserves it unchanged and does not calculate totals, taxes, payment details, or jurisdictional values.

- Supply `taxesApplied` explicitly.
- Ensure totals and line values are internally consistent before calling the SDK.
- Put issuer-supplied payment presentation data in `ReceiptPayload.paymentDetails`.
- Use `IdentificationDetails` for recipient resolution and payment context. Matching data is not copied into `paymentDetails`.

`paymentDetails` remains inside the encrypted receipt body on the digital route. If it is absent, the SDK does not infer it from matching metadata.

### Embedded barcodes

Barcodes and QR codes may be added to the receipt or individual product lines:

```java
Product product = Product.builder()
    .name("Event ticket")
    .identifier("TICKET-001")
    .quantity(1.0)
    .baseQuantity(1.0)
    .unitCode(UnitCode.C62)
    .unitPrice("25.00")
    .subtotal("25.00")
    .total("25.00")
    .addQrCode("https://example.com/tickets/TICKET-001", "Ticket")
    .build();

ReceiptPayload receiptPayload = ReceiptPayload.builder()
    .documentNumber("POS-2026-0002")
    .issueDate(OffsetDateTime.now())
    .currency("EUR")
    .receiptSubtotal("25.00")
    .totalBeforeTax("25.00")
    .totalTaxAmount("0.00")
    .totalAmount("25.00")
    .taxesApplied(false)
    .addProduct(product)
    .addQrCode("https://example.com/returns/POS-2026-0001", "Return code")
    .build();
```

## Delivery Routes and Fallbacks

The backend selects one of three routes:

- `DIGITAL`: the SDK encrypts the payload for every matched owner device and submits it immediately.
- `DOWNLOAD_FALLBACK`: the SDK creates a client-encrypted download when enough local payment context is available, or asks the caller for a final `ReceiptEnvelope`.
- `EMAIL_FALLBACK`: the SDK returns `isEmailReceiptRequired()`. Email delivery is not performed automatically by `issueReceipt`.

Treat `ReceiptResult.getDeliveryRouteType()` as the authoritative route. A failed recipient resolution or invalid request is reported through `CheqiSDKException` rather than a synthetic customer-not-found result.

### Client-encrypted download fallback

When `IdentificationDetails.paymentType` is present and the backend selects `DOWNLOAD_FALLBACK`, `issueReceipt` completes the route automatically. The SDK:

1. Creates a CHEQI JSON document from the definitive `ReceiptPayload` and the locally supplied `IdentificationDetails`.
2. Places it in a `ReceiptEnvelope` and calculates its deterministic hash.
3. Generates a random AES-256-GCM content key and download ID.
4. Uploads only the ciphertext, download ID, and hash.
5. Returns a URL whose fragment contains the content key through `ReceiptResult.getDownloadUrl()`.

The URL fragment is never sent to Cheqi. Anyone who receives the complete URL can decrypt the receipt, so deliver it through an appropriate customer-facing channel.

For an explicit customer-without-Cheqi flow, skip recipient matching:

```java
ReceiptResult result = sdk.getReceiptService().issueDownloadReceipt(
    new IdentificationDetails().paymentType(PaymentType.CASH),
    receiptPayload,
    accessToken
);

String downloadUrl = result.getDownloadUrl();
```

If `issueReceipt` returns `isDownloadEnvelopeRequired()`, generate the final envelope locally and complete the route:

```java
ReceiptResult completed = sdk.getReceiptService().completeDownloadFallback(
    result,
    receiptEnvelope,
    templateHash,
    accessToken
);
```

## Lower-Level Operations

Use the service APIs when you need to control matching, encryption, or submission separately:

```java
RecipientResolutionResponse resolution = sdk.getMatchingService()
    .matchCustomer(identificationDetails, accessToken);

EncryptedReceiptPayload delivery = sdk.getEncryptionService()
    .encryptReceiptForRecipient(receiptPayloadJson, resolution.getRecipients().get(0));

ReceiptSubmissionResponse response = sdk.getReceiptService()
    .submitEncryptedReceipt(encryptedReceiptEnvelope, accessToken);
```

The caller is responsible for validating the selected route, encrypting for every owner device, and preserving the same definitive plaintext across those device encryptions.

## Credit Notes

Credit notes use the same recipient-resolution and per-device encryption model. The caller supplies the definitive credit-note payload and the parent Cheqi receipt ID:

```java
CreditNoteResult result = sdk.getCreditNoteService().issueCreditNote(
    identificationDetails,
    parentCheqiReceiptId,
    creditNotePayload,
    accessToken
);
```

The SDK serializes the supplied credit-note payload without calculations and submits it through the separate encrypted credit-note endpoint.

## Receipt Decryption

A recipient can decrypt a queued, complete receipt envelope with the corresponding device private key:

```java
ReceiptEnvelope receiptEnvelope = sdk.getDecryptionService()
    .decryptReceipt(receiptDelivery, privateKeyBase64);
```

The decrypted payload is already a complete `ReceiptEnvelope`; there is no backend-context merge step in this SDK.

## Verification

The SDK includes deterministic hashing helpers for receipt integrity checks:

```java
String hash = sdk.getVerificationService()
    .calculateCheqiReceiptHash(cheqiReceiptJson);
```

## Store Management

Store operations require an OAuth access token with the relevant store permissions:

```java
StoreDTO store = sdk.getStoreService()
    .createStore(companyId, createStoreRequest, accessToken);

List<StoreDTO> stores = sdk.getStoreService()
    .getStores(companyId, accessToken);

StoreDTO updated = sdk.getStoreService()
    .updateStore(companyId, storeId, updateStoreRequest, accessToken);
```

## Error Handling

High-level receipt and credit-note methods throw `CheqiSDKException`. Direct HTTP client methods throw `CheqiApiException`.

```java
try {
    ReceiptResult result = sdk.getReceiptService()
        .issueReceipt(identificationDetails, receiptPayload, accessToken);
} catch (CheqiSDKException exception) {
    System.err.println(exception.getMessage());
    System.err.println("Error code: " + exception.getErrorCode());
    if (exception.hasCorrelationId()) {
        System.err.println("Correlation ID: " + exception.getCorrelationId());
    }
}
```

## Development

Run the tests:

```bash
./gradlew test
```

Or with Maven:

```bash
mvn test
```

Build the SDK:

```bash
./gradlew build
```

Regenerate the OpenAPI models after updating `openapi.yaml`:

```bash
make generate
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines and [SECURITY.md](SECURITY.md) for vulnerability reporting.
