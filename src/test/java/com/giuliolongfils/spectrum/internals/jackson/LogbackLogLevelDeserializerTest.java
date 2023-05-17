package com.giuliolongfils.spectrum.internals.jackson;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.stream.Stream;

import static ch.qos.logback.classic.Level.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogbackLogLevelDeserializer")
class LogbackLogLevelDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private LogbackLogLevelDeserializer logbackLogLevelDeserializer;

    @DisplayName("deserialize should return the Logback level from the provided string")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final Level expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(expected, logbackLogLevelDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("OFF", OFF),
                arguments("TRACE", TRACE),
                arguments("DEBUG", DEBUG),
                arguments("INFO", INFO),
                arguments("WARN", WARN),
                arguments("default", DEBUG)
        );
    }
}