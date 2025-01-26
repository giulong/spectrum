package io.github.giulong.spectrum.utils.web_driver_events;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuperBuilder
public class LogConsumer extends WebDriverEventConsumer {

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        log
                .atLevel(webDriverEvent.getLevel())
                .log(webDriverEvent.removeTagsFromMessage());
    }
}
