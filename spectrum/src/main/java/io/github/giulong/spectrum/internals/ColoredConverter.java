package io.github.giulong.spectrum.internals;

import static ch.qos.logback.classic.Level.*;
import static ch.qos.logback.core.pattern.color.ANSIConstants.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class ColoredConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    @Override
    protected String getForegroundColorCode(final ILoggingEvent event) {
        return switch (event.getLevel().toInt()) {
            case ERROR_INT -> BOLD + RED_FG;
            case WARN_INT -> YELLOW_FG;
            case INFO_INT -> BLUE_FG;
            case TRACE_INT -> MAGENTA_FG;
            default -> DEFAULT_FG;
        };
    }
}
