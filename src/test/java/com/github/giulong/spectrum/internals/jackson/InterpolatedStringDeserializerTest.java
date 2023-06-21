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

import static com.github.giulong.spectrum.SpectrumSessionListener.VARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterpolatedStringDeserializer")
class InterpolatedStringDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private InterpolatedStringDeserializer interpolatedStringDeserializer;

    private static final String varInEnv = "varInEnv";

    @BeforeAll
    public static void beforeAll() {
        VARS.put("varInEnv", varInEnv);
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final String expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("key");

        assertEquals(expected, interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("value", "value"),
                arguments("${not.set:-local}", "local"),
                arguments("${notSet:-local}", "local"),
                arguments("${notSet:-local.dots}", "local.dots"),
                arguments("${varInEnv:-local}", varInEnv),
                arguments("${varInEnv}", varInEnv),
                arguments("${varInEnv:-~/local}", varInEnv),
                arguments("${not.set}", "${not.set}"),
                arguments("${notSet}", "${notSet}")
        );
    }
}
