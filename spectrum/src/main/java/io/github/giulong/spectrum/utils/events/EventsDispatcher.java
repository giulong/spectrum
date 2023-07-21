package io.github.giulong.spectrum.utils.events;

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

    public static final String BEFORE = "before";
    public static final String AFTER = "after";
    public static final String TEST = "test";
    public static final String CLASS = "class";
    public static final String SUITE = "suite";

    private List<EventsConsumer> consumers;

    public void fire(final String reason, final Set<String> tags) {
        fire(null, null, reason, null, tags, null);
    }

    public void fire(final String primaryId, final String reason) {
        fire(primaryId, null, reason, null, null, null);
    }

    public void fire(final String primaryId, final String secondaryId, final String reason) {
        fire(primaryId, secondaryId, reason, null, null, null);
    }

    public void fire(final String primaryId, final String secondaryId, final String reason, final Result result, final Set<String> tags, final ExtensionContext context) {
        final Event event = Event.builder()
                .primaryId(primaryId)
                .secondaryId(secondaryId)
                .reason(reason)
                .result(result)
                .tags(tags)
                .context(context)
                .build();

        log.debug("Dispatching event {}", event);
        consumers.forEach(consumer -> consumer.match(event));
    }
}
