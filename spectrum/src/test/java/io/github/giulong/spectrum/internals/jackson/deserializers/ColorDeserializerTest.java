package io.github.giulong.spectrum.internals.jackson.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.awt.*;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ColorDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private ColorDeserializer deserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ColorDeserializer.getInstance(), ColorDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should return the duration in seconds from the provided string")
    void deserialize() throws IOException {
        String value = "#ff0000";
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(Color.decode(value), deserializer.deserialize(jsonParser, deserializationContext));
    }
}
