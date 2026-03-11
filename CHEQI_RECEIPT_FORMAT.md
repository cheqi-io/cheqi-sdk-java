# Cheqi Receipt Format

The Cheqi native receipt format is a comprehensive JSON structure that extends the receipt template with payment information and system metadata.

## Structure Overview

```
CheqiReceipt
├─ System Metadata (receipt ID, status, timestamps)
├─ Receipt Template Data (products, taxes, totals)
└─ Payment Information (PAR, card details, payment method)
```

## Key Components

### 1. CheqiReceipt
The main receipt object containing all receipt information.

**Added fields beyond ReceiptTemplateRequest:**
- `receiptId` - System-generated unique identifier
- `status` - Receipt lifecycle status (PENDING, SENT, DELIVERED, etc.)
- `createdAt` - When the receipt was created
- `updatedAt` - When the receipt was last updated
- `deliveredAt` - When the receipt was delivered
- `payment` - Payment information object

### 2. Payment
Payment information including:

**For Card Payments:**
- `par` - Payment Account Reference (unique, non-sensitive card identifier)
- `cardIssuer` - Card network (Visa, Mastercard, etc.)
- `cardBrand` - Specific card type (Visa Debit, etc.)
- `lastFourDigits` - Last 4 digits of card
- `authorizationCode` - Payment processor auth code

**For Cash Payments:**
- `amountTendered` - Cash given by customer
- `changeGiven` - Change returned

**General Fields:**
- `paymentMethod` - CARD, CASH, BANK_TRANSFER, etc.
- `transactionReference` - Transaction ID
- `processor` - Payment processor name
- `terminalId` - POS terminal identifier

### 3. Payment Account Reference (PAR)

The PAR is a **non-financial identifier** that:
- ✅ Remains constant across card replacements
- ✅ Is non-sensitive (no PCI compliance concerns)
- ✅ Enables customer recognition without storing card numbers
- ✅ Facilitates receipt matching

**Example:** Even if a customer's card is replaced due to expiry, loss, or theft, the PAR stays the same, allowing you to match receipts to the same customer.

## Usage Examples

### Example 1: Card Payment Receipt

```java
CheqiReceipt receipt = CheqiReceipt.builder()
    // System metadata
    .receiptId(UUID.randomUUID())
    .status(ReceiptStatus.SENT)
    .createdAt(Instant.now())
    
    // Receipt template data
    .documentNumber("INV-2024-001")
    .issueDate(Instant.now())
    .currency("EUR")
    .receiptSubtotal(new BigDecimal("250.00"))
    .totalBeforeTax(new BigDecimal("250.00"))
    .totalTaxAmount(new BigDecimal("52.50"))
    .totalAmount(new BigDecimal("302.50"))
    
    // Products
    .addProduct(Product.builder()
        .name("Nike AirMax")
        .quantity(2.0)
        .unitPrice("125.00")
        .subtotal("250.00")
        .total("302.50")
        .addTax(21.0, "VAT", "52.50")
        .build())
    
    // Taxes
    .addTax(Tax.builder()
        .rate(21.0)
        .type("VAT")
        .amount(new BigDecimal("52.50"))
        .build())
    
    // Payment information
    .payment(Payment.builder()
        .paymentMethod(PaymentMethod.CARD)
        .par("PAR1234567890ABCDEF")  // Payment Account Reference
        .cardIssuer("Visa")
        .cardBrand("Visa Debit")
        .lastFourDigits("4242")
        .transactionReference("TXN-2024-001")
        .authorizationCode("AUTH123456")
        .processor("Stripe")
        .terminalId("TERM-001")
        .build())
    
    .build();
```

### Example 2: Cash Payment Receipt

```java
CheqiReceipt receipt = CheqiReceipt.builder()
    .receiptId(UUID.randomUUID())
    .status(ReceiptStatus.SENT)
    .createdAt(Instant.now())
    .documentNumber("CASH-2024-001")
    .issueDate(Instant.now())
    .currency("EUR")
    .receiptSubtotal(new BigDecimal("46.45"))
    .totalBeforeTax(new BigDecimal("46.45"))
    .totalTaxAmount(new BigDecimal("9.75"))
    .totalAmount(new BigDecimal("56.20"))
    .addProduct(...)
    
    // Cash payment
    .payment(Payment.builder()
        .paymentMethod(PaymentMethod.CASH)
        .amountTendered(new BigDecimal("60.00"))
        .changeGiven(new BigDecimal("3.80"))
        .transactionReference("CASH-2024-001")
        .terminalId("CASH-REGISTER-1")
        .build())
    
    .build();
```

### Example 3: Converting Template to CheqiReceipt

```java
// You have a ReceiptTemplateRequest from POS
ReceiptTemplateRequest template = ReceiptTemplateRequest.builder()
    .documentNumber("INV-001")
    .issueDate(Instant.now())
    // ... other template fields
    .build();

// Convert to full CheqiReceipt with system metadata and payment
CheqiReceipt receipt = CheqiReceipt.fromTemplate(template)
    .toBuilder()  // Get builder from converted receipt
    .payment(Payment.builder()
        .paymentMethod(PaymentMethod.CARD)
        .par("PAR123...")
        .cardIssuer("Mastercard")
        .lastFourDigits("5678")
        .build())
    .build();
```

## Receipt Status Lifecycle

```
PENDING → PROCESSING → SENT → DELIVERED
                          ↓
                       FAILED
```

**Status transitions:**
1. `PENDING` - Receipt created, waiting for processing
2. `PROCESSING` - Customer matching in progress
3. `SENT` - Delivered to customer device/email/webhook
4. `DELIVERED` - Confirmed received by customer
5. `FAILED` - Delivery failed (no customer match, etc.)
6. `CANCELLED` - Receipt voided
7. `REFUNDED` - Receipt refunded

## Payment Methods

- `CARD` - Credit/debit card payment
- `CASH` - Cash payment
- `BANK_TRANSFER` - Wire transfer
- `DIGITAL_WALLET` - Apple Pay, Google Pay, etc.
- `BNPL` - Buy now, pay later (Klarna, Afterpay)
- `CRYPTO` - Cryptocurrency
- `CHECK` - Check/cheque payment
- `DIRECT_DEBIT` - Direct debit
- `VOUCHER` - Gift card or voucher
- `OTHER` - Other payment method

## Format Comparison

### ReceiptTemplateRequest vs CheqiReceipt

| Feature | ReceiptTemplateRequest | CheqiReceipt |
|---------|----------------------|--------------|
| Purpose | Template for creating receipts | Complete receipt with metadata |
| Receipt ID | ❌ No | ✅ Yes |
| Status tracking | ❌ No | ✅ Yes |
| Timestamps | ❌ No | ✅ Yes (created, updated, delivered) |
| Payment info | ❌ No | ✅ Yes (PAR, card details, etc.) |
| Use case | POS → Cheqi API | Cheqi API → Customer/Webhooks |

## JSON Example

```json
{
  "receiptId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "sent",
  "createdAt": "2024-02-03T12:00:00Z",
  "documentNumber": "INV-2024-001",
  "issueDate": "2024-02-03T12:00:00Z",
  "currency": "EUR",
  "receiptSubtotal": 250.00,
  "totalBeforeTax": 250.00,
  "totalTaxAmount": 52.50,
  "totalAmount": 302.50,
  "products": [
    {
      "name": "Nike AirMax",
      "quantity": 2.0,
      "unitPrice": 125.00,
      "subtotal": 250.00,
      "total": 302.50
    }
  ],
  "taxes": [
    {
      "rate": 21.0,
      "type": "VAT",
      "amount": 52.50
    }
  ],
  "payment": {
    "paymentMethod": "card",
    "par": "PAR1234567890ABCDEF",
    "cardIssuer": "Visa",
    "cardBrand": "Visa Debit",
    "lastFourDigits": "4242",
    "transactionReference": "TXN-2024-001",
    "authorizationCode": "AUTH123456"
  }
}
```

## Best Practices

1. **Always include PAR for card payments** - Enables customer recognition across card replacements
2. **Use appropriate PaymentMethod** - Helps with analytics and reporting
3. **Include transaction references** - Crucial for reconciliation and refunds
4. **Set proper status** - Track receipt lifecycle accurately
5. **Include terminal ID** - Useful for physical stores with multiple POS terminals

## Security Considerations

- ✅ **PAR is safe to store** - Non-sensitive, no PCI compliance required
- ✅ **Last 4 digits only** - Never store full card numbers
- ✅ **No CVV/security codes** - Never include in receipts
- ✅ **Authorization codes** - Safe to store for reconciliation
- ⚠️ **Transaction references** - Can be stored but may be sensitive depending on processor
