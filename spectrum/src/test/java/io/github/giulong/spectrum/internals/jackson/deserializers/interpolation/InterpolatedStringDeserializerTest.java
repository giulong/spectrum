package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import io.github.giulong.spectrum.MockFinal;
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

class InterpolatedStringDeserializerTest {

    private MockedStatic<Reflections> reflectionsMockedStatic;

    @MockFinal
    @SuppressWarnings("unused")
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

    @BeforeEach
    void beforeEach() {
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
        assertSame(InterpolatedStringDeserializer.getInstance(), InterpolatedStringDeserializer.getInstance());
    }

    @Test
    @DisplayName("deserialize should delegate to the parent method passing the string value, interpolating the timestamp")
    void deserializeTimestamp() throws IOException {
        final String value = "value-${timestamp}";

        when(jsonParser.getValueAsString()).thenReturn(value);

        // parent interpolate
        when(configuration.getConfig()).thenReturn(config);
        when(config.getInterpolators()).thenReturn(interpolators);
        when(Reflections.getFieldsValueOf(interpolators)).thenReturn(List.of(interpolator));
        when(interpolator.isEnabled()).thenReturn(true);
        when(interpolator.findVariableFor(value, jsonParser)).thenReturn(Optional.of(value));

        assertThat(deserializer.deserialize(jsonParser, deserializationContext), matchesPattern("value-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}"));
        verifyNoInteractions(deserializationContext);
    }
}
