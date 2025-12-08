package io.github.giulong.spectrum.internals.jackson.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

class RandomDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private RandomDeserializer randomDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(RandomDeserializer.getInstance(), RandomDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should return the log level from the provided string")
    void deserialize() throws IOException {
        long value = 42L;
        when(jsonParser.getValueAsLong()).thenReturn(value);

        final MockedConstruction<Random> mockedConstruction = mockConstruction(Random.class, (mock, context) -> assertEquals(value, context.arguments().getFirst()));

        final Random actual = randomDeserializer.deserialize(jsonParser, deserializationContext);
        final Random expected = mockedConstruction.constructed().getFirst();

        assertEquals(expected, actual);

        mockedConstruction.close();
    }
}
