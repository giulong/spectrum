package io.github.giulong.spectrum.utils.web_driver_events;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class WebDriverEventConsumer implements Consumer<WebDriverEvent> {
    private boolean enabled;
}
