package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static io.github.giulong.spectrum.SpectrumSessionListener.VARS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterpolatedStringDeserializer")
class InterpolatedStringDeserializerTest {

    private static final String VAR_IN_ENV = "varInEnv";

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private InterpolatedStringDeserializer interpolatedStringDeserializer;

    @BeforeAll
    public static void beforeAll() {
        VARS.put("varInEnv", VAR_IN_ENV);
    }

    @AfterAll
    public static void afterAll() {
        VARS.clear();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(InterpolatedStringDeserializer.getInstance(), InterpolatedStringDeserializer.getInstance());
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final String expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("not important");

        assertEquals(expected, interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("value", "value"),
                arguments("${not.set:-local}", "local"),
                arguments("${notSet:-local}", "local"),
                arguments("${notSet:-local}-something_else-${varInEnv}", "local-something_else-" + VAR_IN_ENV),
                arguments("${notSet:-local.dots}", "local.dots"),
                arguments("${notSet:-}", ""),
                arguments("${varInEnv:-local}", VAR_IN_ENV),
                arguments("${varInEnv}", VAR_IN_ENV),
                arguments("${varInEnv:-~/local}", VAR_IN_ENV),
                arguments("${not.set}", "${not.set}"),
                arguments("${notSet}", "${notSet}")
        );
    }

    @Test
    @DisplayName("deserialize should consider system properties")
    public void deserializeFromSystemProperty() throws Exception {
        final String expected = "expected";
        System.setProperty("systemProperty", expected);

        when(jsonParser.getValueAsString()).thenReturn("${systemProperty:-local}");
        when(jsonParser.currentName()).thenReturn("not important");

        // We set the "systemProperty" env var with a random value just to check the precedence: system property wins
        withEnvironmentVariable("systemProperty", "SOME VALUE").execute(() -> assertEquals(expected, interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext)));

        System.clearProperty("systemProperty");
    }

    @Test
    @DisplayName("deserialize should consider env variables")
    public void deserializeFromEnvVariables() throws Exception {
        final String expected = "expected";

        when(jsonParser.getValueAsString()).thenReturn("${envVar:-local}");
        when(jsonParser.currentName()).thenReturn("not important");

        withEnvironmentVariable("envVar", expected).execute(() -> assertEquals(expected, interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext)));
    }
}
