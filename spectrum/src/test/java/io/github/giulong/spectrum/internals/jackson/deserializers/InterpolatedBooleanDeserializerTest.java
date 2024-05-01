package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.Vars;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterpolatedBooleanDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @Mock
    private Configuration configuration;

    @Mock
    private Set<String> interpolationVars;

    @InjectMocks
    private InterpolatedBooleanDeserializer interpolatedBooleanDeserializer;

    private static final String varInEnv = "varInEnv";

    @BeforeAll
    public static void beforeAll() {
        Vars.getInstance().put("varInEnv", varInEnv);
    }

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("configuration", interpolatedBooleanDeserializer, configuration);
    }

    @AfterAll
    public static void afterAll() {
        Vars.getInstance().clear();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(InterpolatedBooleanDeserializer.getInstance(), InterpolatedBooleanDeserializer.getInstance());
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {3}")
    @MethodSource("valuesProvider")
    public void deserialize(final String value, final int times, final String varName, final boolean expected) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(value);
        when(jsonParser.currentName()).thenReturn("key");
        when(configuration.getInterpolationVars()).thenReturn(interpolationVars);

        assertEquals(expected, interpolatedBooleanDeserializer.deserialize(jsonParser, deserializationContext));

        verify(interpolationVars, times(times)).add(varName);
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("true", 0, "", true),
                arguments("false", 0, "", false),
                arguments("${not.set:-true}", 1, "not.set", true),
                arguments("${notSet:-true}", 1, "notSet", true),
                arguments("${notSet:-}", 1, "notSet", false),
                arguments("${varInEnv:-true}", 1, "varInEnv", false),
                arguments("${varInEnv}", 1, "varInEnv", false),
                arguments("${not.set}", 1, "not.set", false),
                arguments("${notSet}", 1, "notSet", false)
        );
    }
}
