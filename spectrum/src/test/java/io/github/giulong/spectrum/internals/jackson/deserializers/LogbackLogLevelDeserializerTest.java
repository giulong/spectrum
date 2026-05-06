package io.github.giulong.spectrum.internals.jackson.deserializers;

import static ch.qos.logback.classic.Level.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import ch.qos.logback.classic.Level;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

class LogbackLogLevelDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private LogbackLogLevelDeserializer logbackLogLevelDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(LogbackLogLevelDeserializer.getInstance(), LogbackLogLevelDeserializer.getInstance());
    }

    @DisplayName("deserialize should return the Logback level from the provided string")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void deserialize(final String value, final Level expected) {
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(expected, logbackLogLevelDeserializer.deserialize(jsonParser, deserializationContext));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("OFF", OFF),
                arguments("TRACE", TRACE),
                arguments("DEBUG", DEBUG),
                arguments("INFO", INFO),
                arguments("WARN", WARN),
                arguments("default", DEBUG));
    }
}
