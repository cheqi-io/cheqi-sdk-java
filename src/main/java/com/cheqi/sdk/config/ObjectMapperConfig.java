package com.cheqi.sdk.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Centralized ObjectMapper configuration for the Cheqi SDK.
 * 
 * This class provides a singleton ObjectMapper instance that mirrors the backend's
 * JacksonConfig to ensure consistent JSON serialization across the entire SDK,
 * particularly for BigDecimal and Double values.
 * 
 * Usage:
 * <pre>
 * ObjectMapper mapper = ObjectMapperConfig.getInstance();
 * String json = mapper.writeValueAsString(object);
 * </pre>
 */
public final class ObjectMapperConfig {

    private static final ObjectMapper INSTANCE = createConfiguredObjectMapper();

    // Private constructor to prevent instantiation
    private ObjectMapperConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the singleton ObjectMapper instance configured for the Cheqi SDK.
     * 
     * @return Configured ObjectMapper instance
     */
    public static ObjectMapper getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a configured ObjectMapper that matches the backend's JacksonConfig.
     * This ensures consistent JSON serialization across backend and SDK.
     */
    private static ObjectMapper createConfiguredObjectMapper() {
        SimpleModule trailingZeroRemovalModule = new SimpleModule("TrailingZeroRemovalModule");
        
        // Configure to strip trailing zeros from BigDecimal values
        trailingZeroRemovalModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeNumber(value.stripTrailingZeros().toPlainString());
            }
        });
        
        // Configure to strip trailing zeros from Double values (like percent fields)
        trailingZeroRemovalModule.addSerializer(Double.class, new JsonSerializer<Double>() {
            @Override
            public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value != null) {
                    // Remove trailing zeros by converting to BigDecimal and back
                    BigDecimal bd = BigDecimal.valueOf(value).stripTrailingZeros();
                    gen.writeNumber(bd.toPlainString());
                } else {
                    gen.writeNull();
                }
            }
        });

        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .registerModule(trailingZeroRemovalModule)
                .registerModule(new Jdk8Module())      // Handles Optional, Stream, etc.
                .registerModule(new JavaTimeModule()); // Handles LocalDateTime, LocalDate, etc.
    }
}
