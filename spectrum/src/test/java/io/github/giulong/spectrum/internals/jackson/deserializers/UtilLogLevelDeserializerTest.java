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

import static java.util.logging.Level.INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilLogLevelDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private UtilLogLevelDeserializer utilLogLevelDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(UtilLogLevelDeserializer.getInstance(), UtilLogLevelDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should return the log level from the provided string")
    public void deserialize() throws IOException {
        String value = "INFO";
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(INFO, utilLogLevelDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
