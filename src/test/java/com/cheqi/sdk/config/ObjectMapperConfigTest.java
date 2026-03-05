package com.cheqi.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperConfigTest {

    @Test
    void singletonReturnsSameInstance() {
        ObjectMapper first = ObjectMapperConfig.getInstance();
        ObjectMapper second = ObjectMapperConfig.getInstance();
        assertSame(first, second);
    }

    @Test
    void bigDecimalTrailingZerosStripped() throws Exception {
        ObjectMapper mapper = ObjectMapperConfig.getInstance();
        Map<String, BigDecimal> data = Map.of("amount", new BigDecimal("100.500000"));

        String json = mapper.writeValueAsString(data);

        assertTrue(json.contains("100.5"), "Trailing zeros should be stripped. Got: " + json);
        assertFalse(json.contains("100.500000"), "Raw trailing zeros should not appear");
    }

    @Test
    void instantSerializedAsIsoString() throws Exception {
        ObjectMapper mapper = ObjectMapperConfig.getInstance();
        Instant instant = Instant.parse("2024-06-15T14:30:00Z");
        Map<String, Instant> data = Map.of("timestamp", instant);

        String json = mapper.writeValueAsString(data);

        assertTrue(json.contains("2024-06-15T14:30:00Z"),
                "Instant should be ISO-8601 string, not timestamp. Got: " + json);
    }
}
