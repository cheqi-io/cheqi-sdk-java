package com.cheqi.sdk.validation;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.models.generated.ReceiptPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReceiptPayloadValidatorTest {
    private final ReceiptPayloadValidator validator = new ReceiptPayloadValidator();
    private final ObjectMapper mapper = ObjectMapperConfig.getInstance();

    private static String resource(String name) throws Exception {
        try (InputStream input = ReceiptPayloadValidatorTest.class.getResourceAsStream("/validation/" + name)) {
            assertNotNull(input);
            return new String((name.endsWith(".gz") ? new GZIPInputStream(input) : input).readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void bundledCompiledValidatorMatchesEveryNativeReferenceReport() throws Exception {
        JsonNode cases = mapper.readTree(resource("parity-cases.json.gz"));
        assertEquals(42, cases.size());
        for (JsonNode item : cases) {
            InvoiceValidationReport actual = validator.validateJson(item.get("input").asText());
            assertEquals(mapper.readTree(item.get("expected").asText()), mapper.valueToTree(actual),
                    item.get("name").asText());
        }
    }

    @Test
    void allMissingFactsAreAvailableOnTheExistingSdkException() throws Exception {
        ReceiptPayload payload = mapper.readValue(resource("receipt-payload.json"), ReceiptPayload.class);
        payload.getPaymentDetails().setAmountDue(null);
        payload.getPaymentDetails().setRoundingAmount(null);
        payload.getProducts().get(0).getTaxes().get(0).setCategory(null);
        CheqiSDKException error = assertThrows(CheqiSDKException.class, () -> validator.requireValidJson(payload));
        assertTrue(error.isValidationError());
        List<String> paths = new ArrayList<>();
        error.getValidationReport().getIssues().forEach(issue -> paths.add(issue.getPath()));
        assertTrue(paths.contains("paymentDetails.amountDue"));
        assertTrue(paths.contains("paymentDetails.roundingAmount"));
        assertTrue(paths.contains("products[0].taxes[0].category"));
        assertTrue(error.getMessage().contains("paymentDetails.amountDue"));
        assertTrue(error.getMessage().contains("paymentDetails.roundingAmount"));
        assertTrue(error.getMessage().contains("products[0].taxes[0].category"));
        assertThrows(UnsupportedOperationException.class, () -> error.getValidationReport().getIssues().clear());
    }

    @Test
    void serializationPreservesExactMonetaryValuesAndDoesNotModifyTheCaller() throws Exception {
        ReceiptPayload payload = mapper.readValue(resource("receipt-payload.json"), ReceiptPayload.class);
        BigDecimal precise = new BigDecimal("8.9050000000000001");
        payload.getProducts().get(0).setUnitPrice(precise);
        String json = validator.requireValidJson(payload);
        assertEquals("8.9050000000000001", mapper.readTree(json).at("/products/0/unitPrice").asText());
        assertEquals(precise, payload.getProducts().get(0).getUnitPrice());
        assertEquals(new BigDecimal("19.41"), payload.getTotalAmount());
    }

    @Test
    void malformedInputReturnsAnInvalidReport() throws Exception {
        InvoiceValidationReport report = validator.validateJson("{\"private-receipt-value\":");
        assertFalse(report.isValid());
        assertFalse(report.isCompleteDocument());
        assertEquals("CORE-INPUT", report.getIssues().get(0).getRuleId());
    }

    @Test
    void serializationFailuresPreserveTheOriginalCauseAndFieldContext() {
        IllegalArgumentException source = new IllegalArgumentException("POS amount cannot be read");
        ReceiptPayload payload = new ReceiptPayload() {
            @Override public BigDecimal getTotalAmount() { throw source; }
        };
        CheqiSDKException error = assertThrows(CheqiSDKException.class, () -> validator.validate(payload));
        assertTrue(error.isValidationError());
        assertNotNull(error.getCause());
        assertTrue(error.getCause().getMessage().contains("totalAmount"));
        assertSame(source, error.getCause().getCause());
    }

    @Test
    void concurrentCallsHaveIndependentMemories() throws Exception {
        String json = resource("receipt-payload.json");
        var executor = Executors.newFixedThreadPool(4);
        try {
            List<Callable<Boolean>> jobs = new ArrayList<>();
            for (int i = 0; i < 20; i++) jobs.add(() -> validator.validateJson(json).isValid());
            for (var result : executor.invokeAll(jobs)) assertTrue(result.get());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void customersDoNotNeedTheWasmCompilerAtRuntime() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.dylibso.chicory.compiler.MachineFactoryCompiler"));
    }

    @Test
    void changedOrUnverifiedBundledArtifactsFailClosed() throws Exception {
        byte[] wasm;
        JsonNode manifest;
        try (InputStream input = ReceiptPayloadValidator.class.getResourceAsStream("cheqi-receipt-validator.wasm");
             InputStream info = ReceiptPayloadValidator.class.getResourceAsStream("manifest.json")) {
            assertNotNull(input);
            assertNotNull(info);
            wasm = input.readAllBytes();
            manifest = mapper.readTree(info);
        }
        ReceiptPayloadValidator.verifyBundle(wasm, manifest);
        wasm[0] ^= 1;
        assertThrows(IllegalStateException.class, () -> ReceiptPayloadValidator.verifyBundle(wasm, manifest));
    }
}
