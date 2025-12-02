package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators.Interpolator;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Config;
import io.github.giulong.spectrum.utils.Configuration.Config.Interpolators;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.Vars;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

@Disabled
class InterpolatedStringDeserializerTest {

    private static final String VAR_IN_ENV = "varInEnv";

    private MockedStatic<Reflections> reflectionsMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private Config config;

    @Mock
    private Interpolators interpolators;

    @Mock
    private Interpolator interpolator;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private InterpolatedStringDeserializer deserializer;

    @BeforeAll
    public static void beforeAll() {
        Vars.getInstance().put("varInEnv", VAR_IN_ENV);
    }

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", deserializer, configuration);

        reflectionsMockedStatic = mockStatic(Reflections.class);
    }

    @AfterEach
    void afterEach() {
        reflectionsMockedStatic.close();
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
        interpolateStubsFor(value);

        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(expected, deserializer.deserialize(jsonParser, deserializationContext));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("value", "value"),
                arguments("${not.set:-_-}", "_-"),
                arguments("${not.set:-- -}", "- -"),
                arguments("${not.set:--\t-}", "-\t-"),
                arguments("${not.set:-a b }", "a b "),
                arguments("${not.set:-local}", "local"),
                arguments("${notSet:-local}", "local"),
                arguments("${notSet:-local}-something_else-${varInEnv}", "local-something_else-" + VAR_IN_ENV),
                arguments("${notSet:-${varInEnv:-local}}-nested", VAR_IN_ENV + "-nested"),
                arguments("${notSet:-${varInEnv:-local}}-nested-${varInEnv}", VAR_IN_ENV + "-nested-" + VAR_IN_ENV),
                arguments("${notSet:-local.dots}", "local.dots"),
                arguments("${notSet:---key=value}", "--key=value"),
                arguments("${notSet:-}", ""),
                arguments("${varInEnv:-local}", VAR_IN_ENV),
                arguments("${varInEnv}", VAR_IN_ENV),
                arguments("${varInEnv:-~/local}", VAR_IN_ENV),
                arguments("${not.set}", "${not.set}"),
                arguments("${notSet}", "${notSet}"));
    }

    @Test
    @DisplayName("deserialize should interpolate the timestamp")
    void deserializeTimestamp() throws IOException {
        final String value = "value-${timestamp}";

        interpolateStubsFor(value);
        when(jsonParser.getValueAsString()).thenReturn(value);

        assertThat(deserializer.deserialize(jsonParser, deserializationContext), matchesPattern("value-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}"));
    }

    @Test
    @DisplayName("deserialize should consider system properties")
    void deserializeFromSystemProperty() throws Exception {
        final String expected = "expected";
        System.setProperty("systemProperty", expected);

        when(jsonParser.getValueAsString()).thenReturn("${systemProperty:-local}");

        // We set the "systemProperty" env var with a random value just to check the precedence: system property wins
        withEnvironmentVariable("systemProperty", "SOME VALUE")
                .execute(() -> assertEquals(expected, deserializer.deserialize(jsonParser, deserializationContext)));

        System.clearProperty("systemProperty");
    }

    @Test
    @DisplayName("deserialize should consider env variables")
    void deserializeFromEnvVariables() throws Exception {
        final String expected = "expected";

        interpolateStubsFor(expected);
        when(jsonParser.getValueAsString()).thenReturn("${envVar:-local}");

        withEnvironmentVariable("envVar", expected).execute(() -> assertEquals(expected, deserializer.deserialize(jsonParser, deserializationContext)));
    }

    private void interpolateStubsFor(final String value) {
        when(configuration.getConfig()).thenReturn(config);
        when(config.getInterpolators()).thenReturn(interpolators);
        when(Reflections.getFieldsValueOf(interpolators)).thenReturn(List.of(interpolator));
        when(interpolator.isEnabled()).thenReturn(true);

        when(interpolator.findVariableFor(value, jsonParser)).thenReturn(Optional.of(value));
    }
}
