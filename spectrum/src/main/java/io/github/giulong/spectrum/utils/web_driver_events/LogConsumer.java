package io.github.giulong.spectrum.utils.web_driver_events;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Builder
public class LogConsumer implements Consumer<WebDriverEvent> {

    private static final String TAG = "<.*?>";

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        log
                .atLevel(webDriverEvent.getLevel())
                .log(webDriverEvent.getMessage().replaceAll(TAG, ""));
    }
}
