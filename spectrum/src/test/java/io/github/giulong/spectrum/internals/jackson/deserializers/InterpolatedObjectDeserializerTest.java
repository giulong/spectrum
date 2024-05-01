package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.Vars;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.node.JsonNodeType.NUMBER;
import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterpolatedObjectDeserializerTest {

    private static final String VAR_IN_ENV = "456";

    private static MockedStatic<InterpolatedStringDeserializer> interpolatedStringDeserializerMockedStatic;

    @Mock
    private InterpolatedStringDeserializer interpolatedStringDeserializer;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @Mock
    private JsonNode jsonNode;

    @Mock
    private Configuration configuration;

    @Mock
    private Set<String> interpolationVars;

    @InjectMocks
    private InterpolatedObjectDeserializer interpolatedObjectDeserializer;

    @BeforeAll
    public static void beforeAll() {
        Vars.getInstance().put("varInEnv", VAR_IN_ENV);
    }

    @BeforeEach
    public void beforeEach() {
        interpolatedStringDeserializerMockedStatic = mockStatic(InterpolatedStringDeserializer.class);
        Reflections.setField("configuration", interpolatedObjectDeserializer, configuration);
    }

    @AfterEach
    public void afterEach() {
        interpolatedStringDeserializerMockedStatic.close();
    }

    @AfterAll
    public static void afterAll() {
        Vars.getInstance().clear();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(InterpolatedObjectDeserializer.getInstance(), InterpolatedObjectDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should delegate to the interpolatedStringDeserializer when the value is a string")
    public void deserializeStrings() throws IOException {
        final String value = "value";
        final String expected = "expected";
        final String currentName = "currentName";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);
        when(InterpolatedStringDeserializer.getInstance()).thenReturn(interpolatedStringDeserializer);
        when(interpolatedStringDeserializer.interpolate(value, currentName)).thenReturn(expected);

        assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @DisplayName("deserialize should return the numberValue when the value is a number")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("numbersValuesProvider")
    public void deserializeNumbers(final String value, final Number expected) throws IOException {
        final String currentName = "currentName";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(NUMBER);
        when(jsonNode.textValue()).thenReturn(value);
        when(jsonNode.numberValue()).thenReturn(expected);

        assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> numbersValuesProvider() {
        return Stream.of(
                arguments("123", 123),
                arguments("123.5", 123.5),
                arguments("-123", -123),
                arguments("-123.5", -123.5)
        );
    }

    @DisplayName("deserialize should return the jsonNode when the value is not a string nor a number")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @EnumSource(value = JsonNodeType.class, mode = EXCLUDE, names = {"STRING", "NUMBER"})
    public void deserializeObjects(final JsonNodeType jsonNodeType) throws IOException {
        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn("currentName");
        when(jsonNode.getNodeType()).thenReturn(jsonNodeType);
        when(jsonNode.textValue()).thenReturn("value");

        assertEquals(jsonNode, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @DisplayName("deserialize should apply the INT_PATTERN when deserializing numbers from strings")
    @ParameterizedTest(name = "with value {0} we expect {2}")
    @MethodSource("valuesProvider")
    public void deserializeStringNumbers(final String value, final String varName, final int expected) throws IOException {
        final String currentName = "currentName";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);
        when(configuration.getInterpolationVars()).thenReturn(interpolationVars);

        assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));

        verify(interpolationVars).add(varName);
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("$<not.set:-123>", "not.set", 123),
                arguments("$<notSet:-123>", "notSet", 123),
                arguments("$<notSet:->", "notSet", 0),
                arguments("$<not.set>", "not.set", 0),
                arguments("$<notSet>", "notSet", 0),
                arguments("$<notSet:-stringDefault>", "notSet", 0),
                arguments("$<varInEnv:-123>", "varInEnv", Integer.parseInt(VAR_IN_ENV)),
                arguments("$<varInEnv>", "varInEnv", Integer.parseInt(VAR_IN_ENV))
        );
    }

    @Test
    @DisplayName("deserialize should consider system properties")
    public void deserializeFromSystemProperty() throws Exception {
        final String currentName = "currentName";
        final int expected = 123;
        final String value = "$<systemProperty:-456>";
        System.setProperty("systemProperty", String.valueOf(expected));

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);
        when(configuration.getInterpolationVars()).thenReturn(interpolationVars);

        // We set the "systemProperty" env var with a random value just to check the precedence: system property wins
        withEnvironmentVariable("systemProperty", "SOME VALUE").execute(() -> assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext)));

        verify(interpolationVars).add("systemProperty");
        System.clearProperty("systemProperty");
    }

    @Test
    @DisplayName("deserialize should consider env variables")
    public void deserializeFromEnvVariables() throws Exception {
        final String currentName = "currentName";
        final int expected = 123;
        final String value = "$<envVar:-456>";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);
        when(configuration.getInterpolationVars()).thenReturn(interpolationVars);

        withEnvironmentVariable("envVar", String.valueOf(expected)).execute(() -> assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext)));

        verify(interpolationVars).add("envVar");
    }

    @DisplayName("isNumber should check if the provided string can be parsed to a number")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("isNumberValuesProvider")
    public void isNumber(final String value, final boolean expected) {
        assertEquals(expected, interpolatedObjectDeserializer.isNumber(value));
    }

    public static Stream<Arguments> isNumberValuesProvider() {
        return Stream.of(
                arguments("123", true),
                arguments("123,5", true),
                arguments("123.5", true),
                arguments("", false),
                arguments("-123", true),
                arguments("-123,5", true),
                arguments("-123.5", true),
                arguments("-", false)
        );
    }
}
