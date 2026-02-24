package com.cheqi.sdk.receipt;

import com.cheqi.sdk.models.PaymentInfo;
import com.cheqi.sdk.models.PaymentMethod;
import com.cheqi.sdk.models.PaymentType;
import com.cheqi.sdk.models.Payment;
import com.cheqi.sdk.models.ubl.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps the simplified PaymentInfo model (from the matching API) to format-specific
 * payment models used in UBL and Cheqi receipt formats.
 */
public class PaymentInfoMapper {

    /**
     * Maps PaymentInfo to UBL PaymentMeans.
     *
     * @param paymentInfo simplified payment info from matching response
     * @return UBL PaymentMeans, or null if paymentInfo is null
     */
    public static PaymentMeans toUblPaymentMeans(PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return null;
        }

        Code paymentMeansCode = new Code.Builder(paymentInfo.getPaymentType().getValue()).build();
        PaymentMeans.Builder builder = PaymentMeans.builder(paymentMeansCode);

        // Map payment IDs
        if (paymentInfo.getPaymentIds() != null && !paymentInfo.getPaymentIds().isEmpty()) {
            List<Identifier> paymentIds = paymentInfo.getPaymentIds().stream()
                    .map(id -> new Identifier.Builder(id).build())
                    .collect(Collectors.toList());
            builder.paymentID(paymentIds);
        }

        // Map card payment fields
        if (paymentInfo.getPaymentType() == PaymentType.CARD_PAYMENT) {
            String primaryId = paymentInfo.getPar() != null ? paymentInfo.getPar() : paymentInfo.getLastFourDigits();
            if (primaryId != null) {
                Identifier networkId = paymentInfo.getCardProvider() != null
                        ? new Identifier.Builder(paymentInfo.getCardProvider()).build()
                        : new Identifier.Builder("UNKNOWN").build();

                CardAccount cardAccount = CardAccount.builder(
                        new Identifier.Builder(primaryId).build(),
                        networkId
                ).build();

                builder.cardAccount(List.of(cardAccount));
            }
        }

        // Map direct debit / bank transfer fields
        if (paymentInfo.getPaymentType() == PaymentType.DIRECT_DEBIT && paymentInfo.getIban() != null) {
            FinancialAccount.Builder financialAccountBuilder = FinancialAccount.builder()
                    .id(new Identifier.Builder(paymentInfo.getIban()).build());

            if (paymentInfo.getAccountHolderName() != null) {
                financialAccountBuilder.name(Name.builder().value(paymentInfo.getAccountHolderName()).build());
            }

            if (paymentInfo.getBic() != null) {
                FinancialInstitution.Builder fiBuilder = FinancialInstitution.builder()
                        .id(new Identifier.Builder(paymentInfo.getBic()).build());

                if (paymentInfo.getBankName() != null) {
                    fiBuilder.name(Name.builder().value(paymentInfo.getBankName()).build());
                }

                Branch branch = Branch.builder()
                        .financialInstitution(fiBuilder.build())
                        .build();

                financialAccountBuilder.financialInstitutionBranch(branch);
            }

            builder.payeeFinancialAccount(financialAccountBuilder.build());
        }

        return builder.build();
    }

    /**
     * Maps PaymentInfo to Cheqi Payment model.
     *
     * @param paymentInfo simplified payment info from matching response
     * @return Cheqi Payment, or null if paymentInfo is null
     */
    public static Payment toCheqiPayment(PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return null;
        }

        Payment.Builder builder = Payment.builder()
                .paymentMethod(mapPaymentType(paymentInfo.getPaymentType()));

        // Card fields
        if (paymentInfo.getPar() != null) {
            builder.par(paymentInfo.getPar());
        }
        if (paymentInfo.getCardProvider() != null) {
            builder.cardIssuer(paymentInfo.getCardProvider());
        }
        if (paymentInfo.getLastFourDigits() != null) {
            builder.lastFourDigits(paymentInfo.getLastFourDigits());
        }

        // Transaction references from paymentIds
        if (paymentInfo.getPaymentIds() != null && !paymentInfo.getPaymentIds().isEmpty()) {
            builder.transactionReference(paymentInfo.getPaymentIds().get(0));
        }

        return builder.build();
    }

    /**
     * Maps backend PaymentType to SDK PaymentMethod.
     */
    private static PaymentMethod mapPaymentType(PaymentType paymentType) {
        if (paymentType == null) {
            return PaymentMethod.OTHER;
        }
        switch (paymentType) {
            case CARD_PAYMENT:
                return PaymentMethod.CARD;
            case DIRECT_DEBIT:
                return PaymentMethod.DIRECT_DEBIT;
            case CASH:
                return PaymentMethod.CASH;
            default:
                return PaymentMethod.OTHER;
        }
    }
}
