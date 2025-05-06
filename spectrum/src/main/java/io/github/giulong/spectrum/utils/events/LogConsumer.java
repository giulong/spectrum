package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.util.Map;

import static org.slf4j.event.Level.INFO;

@Slf4j
@Getter
public class LogConsumer extends EventsConsumer {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonPropertyDescription("Template to be used when creating the message")
    private final String template = "log.txt";

    @JsonPropertyDescription("Level at which the message is logged")
    private final Level level = INFO;

    @Override
    public void accept(final Event event) {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = freeMarkerWrapper.interpolateTemplate(template, vars);

        log.atLevel(level).log(interpolatedTemplate);
    }
}
