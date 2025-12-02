package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class PropertiesInterpolatorTest {

    private final String property = "property";

    @InjectMocks
    private PropertiesInterpolator interpolator;

    @AfterEach
    void afterEach() {
        System.clearProperty(property);
    }

    @Test
    @DisplayName("getConsumer should return System::getProperty")
    void getConsumer() {
        final String value = "value";

        System.setProperty(property, value);

        assertEquals(value, interpolator.getConsumer().apply(property));
    }
}
