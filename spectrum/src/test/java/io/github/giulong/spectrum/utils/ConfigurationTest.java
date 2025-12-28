package io.github.giulong.spectrum.utils;

import static io.github.giulong.spectrum.enums.Frame.AUTO;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.utils.Configuration.VisualRegression;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConfigurationTest {

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(Configuration.getInstance(), Configuration.getInstance());
    }

    @DisplayName("VisualRegression.shouldCheck should see if the frames list contains the provided one")
    @ParameterizedTest(name = "with frame {0} we expect {1}")
    @MethodSource("valuesProvider")
    void shouldCheck(final Frame frame, final boolean expected) {
        final Configuration configuration = Configuration.getInstance();
        final VisualRegression visualRegression = new VisualRegression();

        Reflections.setField("frames", visualRegression, List.of(AUTO));
        Reflections.setField("visualRegression", configuration, visualRegression);

        assertEquals(expected, Configuration.getInstance().getVisualRegression().shouldCheck(frame));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(AUTO, true),
                arguments(MANUAL, false));
    }
}
