package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClassDeserializer")
class ClassDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private ClassDeserializer classDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(ClassDeserializer.getInstance(), ClassDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should return the class loaded from the provided string fqdn literal")
    public void deserialize() throws IOException {
        final String value = "java.lang.String";
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(String.class, classDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should throw an exception if the provided value is not a valid fqdn literal")
    public void deserializeThrows() throws IOException {
        final String value = "invalid";
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertThrows(ClassNotFoundException.class, () -> classDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
