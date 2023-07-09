package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.EventReason;
import io.github.giulong.spectrum.enums.EventTag;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.Set;

@Builder
@Slf4j
public class EventsDispatcher {

    private List<EventHandler> handlers;

    public void dispatch(final EventReason reason, final Set<EventTag> tags) {
        dispatch(null, null, reason, null, tags, null);
    }

    public void dispatch(final String className, final String testName, final EventReason reason, final Result result, final Set<EventTag> tags, final ExtensionContext context) {
        final Event event = Event.builder()
                .className(className)
                .testName(testName)
                .reason(reason)
                .result(result)
                .tags(tags)
                .context(context)
                .build();

        log.debug("Dispatching event {}", event);
        handlers.forEach(eventHandler -> eventHandler.match(event));
    }
}
