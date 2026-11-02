import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.ReceiptPayload;
import com.cheqi.sdk.receipt.ReceiptService;
import com.cheqi.sdk.validation.ReceiptPayloadValidator;
import java.lang.reflect.Proxy;
import java.nio.file.Path;

/** Offline example using the actual packaged SDK and bundled Rust validator. */
public class PayloadValidationExample {
    public static void main(String[] args) throws Exception {
        ReceiptPayload payload = ObjectMapperConfig.getInstance().readValue(
                Path.of(args[0]).toFile(), ReceiptPayload.class);
        ReceiptPayloadValidator validator = new ReceiptPayloadValidator();
        System.out.println("Original payload valid: " + validator.validate(payload).isValid());

        // Deliberately omit two POS facts; the SDK must report both before any HTTP call.
        payload.getPaymentDetails().setAmountDue(null);
        payload.getPaymentDetails().setRoundingAmount(null);
        CheqiApiClient offlineApi = (CheqiApiClient) Proxy.newProxyInstance(
                CheqiApiClient.class.getClassLoader(), new Class<?>[] {CheqiApiClient.class},
                (proxy, method, arguments) -> {
                    throw new AssertionError("Invalid payload reached the API client");
                });
        ReceiptService receipts = new ReceiptService(offlineApi, new EncryptionService(), new MatchingService(offlineApi));
        try {
            receipts.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), payload);
            throw new AssertionError("Invalid payload was accepted");
        } catch (CheqiSDKException error) {
            if (error.getValidationReport() == null) throw error;
            System.out.println("Issuance stopped locally: " + error.getErrorCode());
            error.getValidationReport().getIssues().forEach(issue ->
                    System.out.println(issue.getRuleId() + " — " + issue.getPath() + ": " + issue.getMessage()));
        }
    }
}
