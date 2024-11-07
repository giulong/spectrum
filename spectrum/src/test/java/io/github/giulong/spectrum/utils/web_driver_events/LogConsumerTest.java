package io.github.giulong.spectrum.utils.web_driver_events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.slf4j.event.Level.INFO;

class LogConsumerTest {

    @Mock
    private WebDriverEvent webDriverEvent;

    @InjectMocks
    private LogConsumer logConsumer;

    @Test
    @DisplayName("accept should log the message without tags at the level of the provided webDriverEvent")
    void accept() {
        final String arg = "args";
        final String tagsMessage = "message <div>" + arg + "</div>";

        when(webDriverEvent.getLevel()).thenReturn(INFO);
        when(webDriverEvent.getMessage()).thenReturn(tagsMessage);

        logConsumer.accept(webDriverEvent);
    }
}
