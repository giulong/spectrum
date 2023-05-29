package com.github.giulong.spectrum.internals.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.BeforeAll;
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

import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.VARS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterpolatedBooleanDeserializer")
class InterpolatedBooleanDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private InterpolatedBooleanDeserializer interpolatedBooleanDeserializer;

    private static final String varInEnv = "varInEnv";

    @BeforeAll
    public static void beforeAll() {
        VARS.put("varInEnv", varInEnv);
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final boolean expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("key");

        assertEquals(expected, interpolatedBooleanDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("true", true),
                arguments("false", false),
                arguments("${not.set:-true}", true),
                arguments("${notSet:-true}", true),
                arguments("${varInEnv:-true}", false),
                arguments("${varInEnv}", false),
                arguments("${not.set}", false),
                arguments("${notSet}", false)
        );
    }
}