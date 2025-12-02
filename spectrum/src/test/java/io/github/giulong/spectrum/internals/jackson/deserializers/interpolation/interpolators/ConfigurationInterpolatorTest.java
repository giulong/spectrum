package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;

import io.github.giulong.spectrum.utils.Vars;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ConfigurationInterpolatorTest {

    private static final String VAR_IN_ENV = "varInEnv";

    private final String currentName = "currentName";

    @Mock
    private JsonParser jsonParser;

    @InjectMocks
    private ConfigurationInterpolator interpolator;

    @BeforeAll
    public static void beforeAll() {
        Vars.getInstance().put("varInEnv", VAR_IN_ENV);
    }

    @AfterAll
    public static void afterAll() {
        Vars.getInstance().clear();
    }

    @DisplayName("findVariableFor should find the right value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void deserialize(final String value, final String expected) throws IOException {
        when(jsonParser.currentName()).thenReturn(currentName);

        assertEquals(Optional.of(expected), interpolator.findVariableFor(value, jsonParser));
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
    @DisplayName("findVariableFor should consider system properties")
    void deserializeFromSystemProperty() throws Exception {
        final String expected = "expected";

        System.setProperty("systemProperty", expected);
        when(jsonParser.currentName()).thenReturn(currentName);

        // We set the "systemProperty" env var with a random value just to check the precedence: system property wins
        withEnvironmentVariable("systemProperty", "SOME VALUE")
                .execute(() -> assertEquals(Optional.of(expected), interpolator.findVariableFor("${systemProperty:-local}", jsonParser)));

        System.clearProperty("systemProperty");
    }

    @Test
    @DisplayName("findVariableFor should consider env variables")
    void deserializeFromEnvVariables() throws Exception {
        final String expected = "expected";

        when(jsonParser.currentName()).thenReturn(currentName);

        withEnvironmentVariable("envVar", expected).execute(() -> assertEquals(Optional.of(expected), interpolator.findVariableFor("${envVar:-local}", jsonParser)));
    }
}
