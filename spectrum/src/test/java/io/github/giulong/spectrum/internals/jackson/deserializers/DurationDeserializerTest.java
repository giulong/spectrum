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
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DurationDeserializer")
class DurationDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private DurationDeserializer durationDeserializer;

    @Test
    @DisplayName("deserialize should return the duration in seconds from the provided string")
    public void deserialize() throws IOException {
        int value = 123;
        when(jsonParser.getValueAsInt()).thenReturn(value);

        assertEquals(Duration.ofSeconds(value), durationDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
