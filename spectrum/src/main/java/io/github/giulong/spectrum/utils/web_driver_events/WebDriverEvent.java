package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.event.Level;

@Getter
@Builder
public class WebDriverEvent {
    final Frame frame;
    final Level level;
    private String message;
}
