package io.github.giulong.spectrum.internals.jackson.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

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
    void deserialize() {
        double value = 123d;
        when(jsonParser.getValueAsDouble()).thenReturn(value);

        assertEquals(Duration.ofMillis((long) (value * 1000)), durationDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
