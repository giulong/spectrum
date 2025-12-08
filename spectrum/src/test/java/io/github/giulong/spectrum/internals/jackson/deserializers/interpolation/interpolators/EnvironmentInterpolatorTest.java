package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class EnvironmentInterpolatorTest {

    private final String property = "property";

    @InjectMocks
    private EnvironmentInterpolator interpolator;

    @AfterEach
    void afterEach() {
        System.clearProperty(property);
    }

    @Test
    @DisplayName("getConsumer should return System::getenv")
    void getConsumer() throws Exception {
        final String value = "value";

        withEnvironmentVariable(property, value)
                .execute(() -> assertEquals(value, interpolator.getConsumer().apply(property)));
    }
}
