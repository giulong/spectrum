package io.github.giulong.spectrum.internals.jackson.deserializers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ClassDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private ClassDeserializer classDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ClassDeserializer.getInstance(), ClassDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should return the class loaded from the provided string fqdn literal")
    void deserialize() throws IOException {
        final String value = "java.lang.String";
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(String.class, classDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should throw an exception if the provided value is not a valid fqdn literal")
    void deserializeThrows() throws IOException {
        final String value = "invalid";
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertThrows(ClassNotFoundException.class, () -> classDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
