package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.Vars;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.node.JsonNodeType.*;
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

    @Mock
    private YAMLMapper objectMapper;

    @InjectMocks
    private InterpolatedObjectDeserializer interpolatedObjectDeserializer;

    @BeforeAll
    public static void beforeAll() {
        Vars.getInstance().put("varInEnv", VAR_IN_ENV);
    }

    @BeforeEach
    void beforeEach() {
        interpolatedStringDeserializerMockedStatic = mockStatic(InterpolatedStringDeserializer.class);

        Reflections.setField("objectMapper", interpolatedObjectDeserializer, objectMapper);
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
    @ParameterizedTest(name = "with value {0}")
    @ValueSource(doubles = {123, 123.5, -123, -123.5})
    void deserializeNumbers(final Number expected) throws IOException {
        final String currentName = "currentName";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(NUMBER);
        when(jsonNode.numberValue()).thenReturn(expected);

        assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @DisplayName("deserialize should return the booleanValue when the value is a boolean")
    @ParameterizedTest(name = "with value {0}")
    @ValueSource(booleans = {true, false})
    void deserializeBooleans(final boolean expected) throws IOException {
        final String currentName = "currentName";

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(BOOLEAN);
        when(jsonNode.booleanValue()).thenReturn(expected);

        assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should traverse the map and interpolate each entry")
    void deserializeMap() throws IOException {
        final String value = "value";
        final String interpolatedValue = "interpolatedValue";
        final int number = 123;
        final String currentName = "currentName";
        final String key1 = "key1";
        final String key2 = "key2";
        final Map<String, Object> map = Map.of(key1, value, key2, number);
        final Map<String, Object> interpolatedMap = Map.of(key1, interpolatedValue, key2, number);

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(OBJECT);

        when(objectMapper.convertValue(jsonNode, Map.class)).thenReturn(map);
        when(InterpolatedStringDeserializer.getInstance()).thenReturn(interpolatedStringDeserializer);
        when(interpolatedStringDeserializer.interpolate(value, currentName)).thenReturn(interpolatedValue);

        assertEquals(interpolatedMap, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should traverse the map and interpolate nested maps and lists")
    void deserializeMapWithNestedMapsAndLists() throws IOException {
        final String value = "value";
        final String interpolatedValue = "interpolatedValue";
        final String number = "$<not.set:-123>";
        final int interpolatedNumber = 123;
        final String currentName = "currentName";
        final String key1 = "key1";
        final String key2 = "key2";
        final String nestedKey1 = "nestedKey1";
        final List<Object> nestedList = List.of(number);
        final List<Object> interpolatedNestedList = List.of(interpolatedNumber);
        final Map<String, Object> nestedMap = Map.of(nestedKey1, value);
        final Map<String, Object> interpolatedNestedMap = Map.of(nestedKey1, interpolatedValue);
        final Map<String, Object> map = Map.of(key1, nestedMap, key2, nestedList);
        final Map<String, Object> interpolatedMap = Map.of(key1, interpolatedNestedMap, key2, interpolatedNestedList);

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(OBJECT);

        when(objectMapper.convertValue(jsonNode, Map.class)).thenReturn(map);
        when(InterpolatedStringDeserializer.getInstance()).thenReturn(interpolatedStringDeserializer);
        when(interpolatedStringDeserializer.interpolate(value, currentName)).thenReturn(interpolatedValue);

        assertEquals(interpolatedMap, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should traverse the list and interpolate each entry")
    void deserializeList() throws IOException {
        final String value = "value";
        final String interpolatedValue = "interpolatedValue";
        final int number = 123;
        final String currentName = "currentName";
        final List<Object> list = List.of(value, number);
        final List<Object> interpolatedList = List.of(interpolatedValue, number);

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(ARRAY);

        when(objectMapper.convertValue(jsonNode, List.class)).thenReturn(list);
        when(InterpolatedStringDeserializer.getInstance()).thenReturn(interpolatedStringDeserializer);
        when(interpolatedStringDeserializer.interpolate(value, currentName)).thenReturn(interpolatedValue);

        assertEquals(interpolatedList, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @DisplayName("deserialize should traverse the list and interpolate nested maps and lists")
    void deserializeListWithNestedMapsAndLists() throws IOException {
        final String value = "value";
        final String interpolatedValue = "interpolatedValue";
        final String number = "$<not.set:-123>";
        final int interpolatedNumber = 123;
        final String currentName = "currentName";
        final String nestedKey1 = "nestedKey1";
        final List<Object> nestedList = List.of(number);
        final List<Object> interpolatedNestedList = List.of(interpolatedNumber);
        final Map<String, Object> nestedMap = Map.of(nestedKey1, value);
        final Map<String, Object> interpolatedNestedMap = Map.of(nestedKey1, interpolatedValue);
        final List<Object> list = List.of(nestedList, nestedMap);
        final List<Object> interpolatedList = List.of(interpolatedNestedList, interpolatedNestedMap);

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn(currentName);
        when(jsonNode.getNodeType()).thenReturn(ARRAY);

        when(objectMapper.convertValue(jsonNode, List.class)).thenReturn(list);
        when(InterpolatedStringDeserializer.getInstance()).thenReturn(interpolatedStringDeserializer);
        when(interpolatedStringDeserializer.interpolate(value, currentName)).thenReturn(interpolatedValue);

        assertEquals(interpolatedList, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @DisplayName("deserialize should return the jsonNode when the value is not string, number, object, array")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @EnumSource(value = JsonNodeType.class, mode = EXCLUDE, names = {"STRING", "NUMBER", "OBJECT", "ARRAY", "BOOLEAN"})
    void deserializeDefault(final JsonNodeType jsonNodeType) throws IOException {
        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(jsonParser.currentName()).thenReturn("currentName");
        when(jsonNode.getNodeType()).thenReturn(jsonNodeType);

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

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("$<not.set:-123>", 123),
                arguments("$<notSet:-123>", 123),
                arguments("$<notSet:->", 0),
                arguments("$<not.set>", 0),
                arguments("$<notSet>", 0),
                arguments("$<notSet:-~123stringDefault=va:lue.\\/>", 0),
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
        withEnvironmentVariable("systemProperty", "SOME VALUE")
                .execute(() -> assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext)));

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

        withEnvironmentVariable("envVar", String.valueOf(expected))
                .execute(() -> assertEquals(expected, interpolatedObjectDeserializer.deserialize(jsonParser, deserializationContext)));
    }

    @DisplayName("isNumber should check if the provided string can be parsed to a number")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("isNumberValuesProvider")
    void isNumber(final String value, final boolean expected) {
        assertEquals(expected, interpolatedObjectDeserializer.isNumber(value));
    }

    static Stream<Arguments> isNumberValuesProvider() {
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
