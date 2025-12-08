package io.github.giulong.spectrum.internals.jackson.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class DurationDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private DurationDeserializer durationDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(DurationDeserializer.getInstance(), DurationDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should return the duration in seconds from the provided string")
    void deserialize() throws IOException {
        double value = 123d;
        when(jsonParser.getValueAsDouble()).thenReturn(value);

        assertEquals(Duration.ofMillis((long) (value * 1000)), durationDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
