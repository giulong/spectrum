package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.utils.Vars;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

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
        Vars.getInstance().put("varInEnv", VAR_IN_ENV);
    }

    @AfterAll
    public static void afterAll() {
        Vars.getInstance().clear();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(InterpolatedStringDeserializer.getInstance(), InterpolatedStringDeserializer.getInstance());
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void deserialize(final String value, final String expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("not important");

        assertEquals(expected, interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("value", "value"),
                arguments("${not.set:-_-}", "_-"),
                arguments("${not.set:-- -}", "- -"),
                arguments("${not.set:--\t-}", "-\t-"),
                arguments("${not.set:-a b }", "a b "),
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
    @DisplayName("deserialize should interpolate the timestamp")
    void deserializeTimestamp() throws IOException {
        final String value = "value-${timestamp}";

        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("not important");

        assertThat(interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext), matchesPattern("value-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}"));
    }

    @Test
    @DisplayName("deserialize should consider system properties")
    void deserializeFromSystemProperty() throws Exception {
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
    void deserializeFromEnvVariables() throws Exception {
        final String expected = "expected";

        when(jsonParser.getValueAsString()).thenReturn("${envVar:-local}");
        when(jsonParser.currentName()).thenReturn("not important");

        withEnvironmentVariable("envVar", expected).execute(() -> assertEquals(expected, interpolatedStringDeserializer.deserialize(jsonParser, deserializationContext)));
    }
}
