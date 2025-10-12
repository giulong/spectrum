package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.mockito.Mockito.when;

class LogConsumerTest {

    @Mock
    private Event event;

    @MockSingleton
    @SuppressWarnings("unused")
    private FreeMarkerWrapper freeMarkerWrapper;

    @InjectMocks
    private LogConsumer logConsumer;

    @Test
    @DisplayName("accept should log the message at the provided level interpolating the provided template")
    void accept() {
        final String interpolatedTemplate = "interpolatedTemplate";
        when(freeMarkerWrapper.interpolateTemplate("log.txt", Map.of("event", event))).thenReturn(interpolatedTemplate);

        logConsumer.accept(event);
    }
}
