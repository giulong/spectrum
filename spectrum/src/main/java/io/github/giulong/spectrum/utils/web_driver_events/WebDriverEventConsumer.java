package io.github.giulong.spectrum.utils.web_driver_events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.function.Consumer;

@Getter
@SuperBuilder
public abstract class WebDriverEventConsumer implements Consumer<WebDriverEvent> {
    private boolean enabled;
}
