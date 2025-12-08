package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class InterpolatedBooleanDeserializerTest {

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
    private InterpolatedBooleanDeserializer deserializer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", deserializer, configuration);

        reflectionsMockedStatic = mockStatic(Reflections.class);
    }

    @AfterEach
    void afterEach() {
        reflectionsMockedStatic.close();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(InterpolatedBooleanDeserializer.getInstance(), InterpolatedBooleanDeserializer.getInstance());
    }

    @DisplayName("deserialize should delegate to the parent method passing the string value")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    void deserialize(final String value, final boolean expected) throws IOException {
        interpolateStubsFor(value);

        when(jsonParser.getValueAsString()).thenReturn(value);

        assertEquals(expected, deserializer.deserialize(jsonParser, deserializationContext));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("true", true),
                arguments("false", false));
    }

    private void interpolateStubsFor(final String value) {
        when(configuration.getConfig()).thenReturn(config);
        when(config.getInterpolators()).thenReturn(interpolators);
        when(Reflections.getFieldsValueOf(interpolators)).thenReturn(List.of(interpolator));
        when(interpolator.isEnabled()).thenReturn(true);

        when(interpolator.findVariableFor(value, jsonParser)).thenReturn(Optional.of(value));
    }
}
