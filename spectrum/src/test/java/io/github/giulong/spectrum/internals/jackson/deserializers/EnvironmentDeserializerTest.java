package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.utils.environments.AppiumEnvironment;
import io.github.giulong.spectrum.utils.environments.Environment;
import io.github.giulong.spectrum.utils.environments.GridEnvironment;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnvironmentDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private EnvironmentDeserializer environmentDeserializer;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(EnvironmentDeserializer.getInstance(), EnvironmentDeserializer.getInstance());
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void deserialize(final String value, final Environment expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("key");

        assertInstanceOf(expected.getClass(), environmentDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should throw an exception if the provided key is not a valid environment name")
    void deserializeNotExisting() throws IOException {
        String notValidEnvironment = "notValidEnvironment";
        when(jsonParser.getValueAsString()).thenReturn(notValidEnvironment);
        when(jsonParser.currentName()).thenReturn("key");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> environmentDeserializer.deserialize(jsonParser, deserializationContext));
        assertEquals("Value '" + notValidEnvironment + "' is not a valid environment!", exception.getMessage());
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("${justToTestInterpolation:-local}", mock(LocalEnvironment.class)),
                arguments("local", mock(LocalEnvironment.class)),
                arguments("grid", mock(GridEnvironment.class)),
                arguments("appium", mock(AppiumEnvironment.class))
        );
    }
}