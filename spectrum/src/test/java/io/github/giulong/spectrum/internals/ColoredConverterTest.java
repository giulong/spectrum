package io.github.giulong.spectrum.internals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.stream.Stream;

import static ch.qos.logback.classic.Level.*;
import static ch.qos.logback.core.pattern.color.ANSIConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

class ColoredConverterTest {

    @Mock
    private LoggingEvent loggingEvent;

    @InjectMocks
    private ColoredConverter coloredConverter;

    @DisplayName("getForegroundColorCode should override the default logback colors")
    @ParameterizedTest(name = "with level {0} we expect {1}")
    @MethodSource("valuesProvider")
    void getForegroundColorCode(final Level level, final String expected) {
        when(loggingEvent.getLevel()).thenReturn(level);
        assertEquals(expected, coloredConverter.getForegroundColorCode(loggingEvent));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(ERROR, BOLD + RED_FG),
                arguments(WARN, YELLOW_FG),
                arguments(INFO, BLUE_FG),
                arguments(TRACE, MAGENTA_FG),
                arguments(DEBUG, DEFAULT_FG)
        );
    }
}
