package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class InterpolatedDeserializerTest {

    private final String value = "value";

    private MockedStatic<Reflections> reflectionsMockedStatic;

    @Mock
    private Configuration configuration;

    @Mock
    private Config config;

    @Mock
    private Interpolators interpolators;

    @Mock
    private Interpolator interpolator1;

    @Mock
    private Interpolator interpolator2;

    @Mock
    private Interpolator interpolator3;

    @Mock
    private Interpolator interpolator4;

    @Mock
    private JsonParser jsonParser;

    @InjectMocks
    private DummyInterpolatedDeserializer deserializer;

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
    @DisplayName("interpolate should just return the provided value when config is null")
    void interpolateNoConfig() {
        when(configuration.getConfig()).thenReturn(null);

        assertEquals(value, deserializer.interpolate(value, jsonParser));
    }

    @Test
    @DisplayName("interpolate should return the last interpolated value of the interpolators which found the key")
    void interpolate() {
        final String interpolatedValue1 = "interpolatedValue1";
        final String interpolatedValue2 = "interpolatedValue2";

        when(configuration.getConfig()).thenReturn(config);
        when(config.getInterpolators()).thenReturn(interpolators);
        when(Reflections.getFieldsValueOf(interpolators)).thenReturn(List.of(interpolator1, interpolator2, interpolator3, interpolator4));
        when(interpolator1.isEnabled()).thenReturn(false);
        when(interpolator2.isEnabled()).thenReturn(true);
        when(interpolator3.isEnabled()).thenReturn(true);
        when(interpolator4.isEnabled()).thenReturn(true);

        when(interpolator2.findVariableFor(value, jsonParser)).thenReturn(Optional.empty());
        when(interpolator3.findVariableFor(value, jsonParser)).thenReturn(Optional.of(interpolatedValue1));
        when(interpolator4.findVariableFor(value, jsonParser)).thenReturn(Optional.of(interpolatedValue2));

        assertEquals(interpolatedValue2, deserializer.interpolate(value, jsonParser));

        verifyNoMoreInteractions(interpolator1);
    }

    @Test
    @DisplayName("interpolate should return the provided value when no interpolators found the key")
    void interpolateEmpty() {
        when(configuration.getConfig()).thenReturn(config);
        when(config.getInterpolators()).thenReturn(interpolators);
        when(Reflections.getFieldsValueOf(interpolators)).thenReturn(List.of(interpolator1, interpolator2));
        when(interpolator1.isEnabled()).thenReturn(false);
        when(interpolator2.isEnabled()).thenReturn(true);

        when(interpolator2.findVariableFor(value, jsonParser)).thenReturn(Optional.empty());

        assertEquals(value, deserializer.interpolate(value, jsonParser));

        verifyNoMoreInteractions(interpolator1);
    }

    private static final class DummyInterpolatedDeserializer extends InterpolatedDeserializer<String> {

        @Override
        public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
            return "";
        }
    }
}
