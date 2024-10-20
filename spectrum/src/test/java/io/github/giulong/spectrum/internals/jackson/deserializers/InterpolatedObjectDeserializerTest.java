package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.giulong.spectrum.utils.Vars;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.node.JsonNodeType.NUMBER;
import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private InterpolatedObjectDeserializer interpolatedObjectDeserializer;

    @BeforeAll
    public static void beforeAll() {
        Vars.getInstance().put("varInEnv", VAR_IN_ENV);
    }

    @BeforeEach
    void beforeEach() {
        interpolatedStringDeserializerMockedStatic = mockStatic(InterpolatedStringDeserializer.class);
    }

    @AfterEach
    void afterEach() {
        interpolatedStringDeserializerMockedStatic.close();
    }

    @AfterAll
    public static void afterAll() {
        Vars.getInstance().clear();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(InterpolatedObjectDeserializer.getInstance(), InterpolatedObjectDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should delegate to the interpolatedStringDeserializer when the value is a string")
    void deserializeStrings() throws IOException {
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
    void deserializeNumbers(final String value, final Number expected) throws IOException {
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
    void deserializeObjects(final JsonNodeType jsonNodeType) throws IOException {
        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn("currentName");
        when(jsonNode.getNodeType()).thenReturn(jsonNodeType);
        when(jsonNode.textValue()).thenReturn("value");

        assertEquals(jsonNode, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @DisplayName("deserialize should apply the INT_PATTERN when deserializing numbers from strings")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void deserializeStringNumbers(final String value, final int expected) throws IOException {
        final String currentName = "currentName";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);

        assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("$<not.set:-123>", 123),
                arguments("$<notSet:-123>", 123),
                arguments("$<notSet:->", 0),
                arguments("$<not.set>", 0),
                arguments("$<notSet>", 0),
                arguments("$<notSet:-stringDefault>", 0),
                arguments("$<varInEnv:-123>", Integer.parseInt(VAR_IN_ENV)),
                arguments("$<varInEnv>", Integer.parseInt(VAR_IN_ENV))
        );
    }

    @Test
    @DisplayName("deserialize should consider system properties")
    void deserializeFromSystemProperty() throws Exception {
        final String currentName = "currentName";
        final int expected = 123;
        final String value = "$<systemProperty:-456>";
        System.setProperty("systemProperty", String.valueOf(expected));

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);

        // We set the "systemProperty" env var with a random value just to check the precedence: system property wins
        withEnvironmentVariable("systemProperty", "SOME VALUE").execute(() -> assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext)));

        System.clearProperty("systemProperty");
    }

    @Test
    @DisplayName("deserialize should consider env variables")
    void deserializeFromEnvVariables() throws Exception {
        final String currentName = "currentName";
        final int expected = 123;
        final String value = "$<envVar:-456>";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(STRING);
        when(jsonNode.textValue()).thenReturn(value);

        withEnvironmentVariable("envVar", String.valueOf(expected)).execute(() -> assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext)));
    }

    @DisplayName("isNumber should check if the provided string can be parsed to a number")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("isNumberValuesProvider")
    void isNumber(final String value, final boolean expected) {
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
