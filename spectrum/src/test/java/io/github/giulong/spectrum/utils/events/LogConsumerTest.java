package io.github.giulong.spectrum.utils.events;

import static org.mockito.Mockito.when;

import java.util.Map;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class LogConsumerTest {

    @Mock
    private Event event;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @InjectMocks
    private LogConsumer logConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("freeMarkerWrapper", logConsumer, freeMarkerWrapper);
    }

    @Test
    @DisplayName("accept should log the message at the provided level interpolating the provided template")
    void accept() {
        final String interpolatedTemplate = "interpolatedTemplate";
        when(freeMarkerWrapper.interpolateTemplate("log.txt", Map.of("event", event))).thenReturn(interpolatedTemplate);

        logConsumer.accept(event);
    }
}
