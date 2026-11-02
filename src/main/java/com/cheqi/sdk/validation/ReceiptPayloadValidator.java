package com.cheqi.sdk.validation;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.models.generated.ReceiptPayload;
import com.cheqi.sdk.utils.HashUtils;
import com.cheqi.sdk.validation.compiled.ReceiptValidatorModule;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;

/**
 * Checks receipt data for missing fields and inconsistent values before issuance.
 */
public final class ReceiptPayloadValidator {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectMapper PAYLOAD_MAPPER = payloadMapper();
    private static final String PROFILE = "EN16931_CORE_2017_V1";
    private static final int MAX_INPUT_BYTES = 2 * 1024 * 1024;

    private static final class ModuleHolder {
        private static final WasmModule MODULE = loadBundledModule();
    }

    /** Checks receipt data and returns all detected validation issues. */
    public InvoiceValidationReport validate(ReceiptPayload payload) throws CheqiSDKException {
        return validateJson(serialize(payload));
    }

    /** Checks receipt data supplied as JSON and returns all detected validation issues. */
    public InvoiceValidationReport validateJson(String json) throws CheqiSDKException {
        if (json == null) {
            throw new CheqiSDKException("Receipt payload cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 0, null);
        }
        try {
            Instance instance = Instance.builder(ModuleHolder.MODULE)
                    .withMachineFactory(ReceiptValidatorModule::create).build();
            Memory memory = instance.memory();
            if (call(instance, "abi_version") != 1 || memory.maximumPages() != 1024) {
                throw new IllegalStateException("Unsupported validator ABI");
            }
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            int pointer = call(instance, "alloc", bytes.length > MAX_INPUT_BYTES ? MAX_INPUT_BYTES + 1 : bytes.length);
            if (pointer != 0) {
                memory.write(pointer, bytes);
                call(instance, "validate");
            }
            int length = call(instance, "result_len");
            int result = call(instance, "result_ptr");
            if (length <= 0 || Integer.toUnsignedLong(result) + length > (long) memory.pages() * 65536) {
                throw new IllegalStateException("Missing validator report");
            }
            InvoiceValidationReport report = MAPPER.readValue(memory.readBytes(result, length), InvoiceValidationReport.class);
            if (!PROFILE.equals(report.getProfile()) || report.isCompleteDocument()
                    || report.isValid() != report.getIssues().isEmpty()) {
                throw new IllegalStateException("Invalid validator report");
            }
            call(instance, "reset");
            return report;
        } catch (Exception | LinkageError error) {
            // Stop processing without exposing receipt contents through errors.
            throw new CheqiSDKException("Receipt payload validator is unavailable",
                    CheqiSDKException.ErrorCodes.CONFIGURATION_ERROR, 0, null);
        }
    }

    /** Returns receipt data as JSON after successful validation; throws if validation fails. */
    public String requireValidJson(ReceiptPayload payload) throws CheqiSDKException {
        String json = serialize(payload);
        InvoiceValidationReport report = validateJson(json);
        if (!report.isValid()) throw CheqiSDKException.forReceiptValidation(report);
        return json;
    }

    private static String serialize(ReceiptPayload payload) throws CheqiSDKException {
        if (payload == null) {
            throw new CheqiSDKException("Receipt payload cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 0, null);
        }
        try {
            return PAYLOAD_MAPPER.writeValueAsString(payload);
        } catch (Exception error) {
            throw new CheqiSDKException("Receipt payload cannot be serialized",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 0, null);
        }
    }

    private static ObjectMapper payloadMapper() {
        SimpleModule exactDecimals = new SimpleModule("ExactReceiptDecimals");
        exactDecimals.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider serializers)
                    throws IOException {
                // Preserve the supplied amount and decimal precision.
                generator.writeString(value.toPlainString());
            }
        });
        return ObjectMapperConfig.getInstance().copy().registerModule(exactDecimals);
    }

    private static int call(Instance instance, String function, long... args) {
        long[] result = instance.export("cheqi_validator_" + function).apply(args);
        return result == null || result.length == 0 ? 0 : (int) result[0];
    }

    private static WasmModule loadBundledModule() {
        try {
            byte[] wasm = resource("cheqi-receipt-validator.wasm");
            JsonNode manifest = MAPPER.readTree(resource("manifest.json"));
            verifyBundle(wasm, manifest);
            return Parser.parse(wasm);
        } catch (IOException error) {
            throw new IllegalStateException("Bundled validator unavailable");
        }
    }

    static void verifyBundle(byte[] wasm, JsonNode manifest) {
        if (manifest.path("abiVersion").asInt() != 1 || manifest.path("sourceDirty").asBoolean(true)
                || !HashUtils.sha256Hex(wasm).equals(manifest.path("sha256").asText())) {
            throw new IllegalStateException("Validator integrity check failed");
        }
    }

    private static byte[] resource(String name) throws IOException {
        try (InputStream input = ReceiptPayloadValidator.class.getResourceAsStream(name)) {
            if (input == null) throw new IOException("Missing validator resource");
            return input.readAllBytes();
        }
    }
}
