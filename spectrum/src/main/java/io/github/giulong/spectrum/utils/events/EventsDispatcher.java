package io.github.giulong.spectrum.utils.events;

import static lombok.AccessLevel.PRIVATE;

import java.util.Optional;
import java.util.Set;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.pojos.events.Event.Payload;
import io.github.giulong.spectrum.utils.Configuration;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class EventsDispatcher implements SessionHook {

    private static final EventsDispatcher INSTANCE = new EventsDispatcher();

    public static final String BEFORE = "before";
    public static final String BEFORE_EXECUTION = "beforeExecution";
    public static final String AFTER = "after";
    public static final String TEST = "test";
    public static final String TEST_FACTORY = "testFactory";
    public static final String DYNAMIC_TEST = "dynamicTest";
    public static final String CLASS = "class";
    public static final String SUITE = "suite";

    private final Configuration configuration = Configuration.getInstance();

    public static EventsDispatcher getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpened() {
        log.debug("Session opened hook");
        fire(BEFORE, Set.of(SUITE));
    }

    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");
        fire(AFTER, Set.of(SUITE), configuration.getSummary().toResult());
    }

    /**
     * Fires an event with the provided reason and tags
     *
     * @param reason event's reason
     * @param tags event's tags
     */
    public void fire(final String reason, final Set<String> tags) {
        fire(null, null, reason, null, tags, null);
    }

    /**
     * Fires an event with the provided reason, tags, and {@link Result}
     *
     * @param reason event's reason
     * @param tags event's tags
     * @param result event's result
     */
    public void fire(final String reason, final Set<String> tags, final Result result) {
        fire(null, null, reason, result, tags, null);
    }

    /**
     * Fires an event with the provided primary id, reason, {@link ExtensionContext},
     * and {@link Payload}
     *
     * @param primaryId event's primary id
     * @param reason event's reason
     * @param context event's extension context
     * @param payload event's payload
     */
    public void fire(final String primaryId, final String reason, final ExtensionContext context, final Payload payload) {
        fire(primaryId, null, reason, null, null, context, payload);
    }

    /**
     * Fires an event with the provided primary id and reason
     *
     * @param primaryId event's primary id
     * @param reason event's reason
     */
    public void fire(final String primaryId, final String reason) {
        fire(primaryId, null, reason, null, null, null);
    }

    /**
     * Fires an event with the provided primary id, secondary id, and reason
     *
     * @param primaryId event's primary id
     * @param secondaryId event's secondary id
     * @param reason event's reason
     */
    public void fire(final String primaryId, final String secondaryId, final String reason) {
        fire(primaryId, secondaryId, reason, null, null, null);
    }

    /**
     * Fires an event with the provided primary id, secondary id, reason, {@link Result}, tags,
     * and {@link ExtensionContext}
     *
     * @param primaryId event's primary id
     * @param secondaryId event's secondary id
     * @param reason event's reason
     * @param result event's result
     * @param tags event's tags
     * @param context event's extension context
     */
    public void fire(final String primaryId, final String secondaryId, final String reason, final Result result, final Set<String> tags,
                     final ExtensionContext context) {
        fire(primaryId, secondaryId, reason, result, tags, context, null);
    }

    /**
     * Fires an event with the provided primary id, secondary id, reason, {@link Result}, tags,
     * {@link ExtensionContext}, and {@link Payload}.
     * This is the main method that allows to provide all the parameters.
     *
     * @param primaryId event's primary id
     * @param secondaryId event's secondary id
     * @param reason event's reason
     * @param result event's result
     * @param tags event's tags
     * @param context event's extension context
     * @param payload event's payload
     */
    public void fire(final String primaryId, final String secondaryId, final String reason, final Result result, final Set<String> tags,
                     final ExtensionContext context, final Payload payload) {
        final Event event = Event.builder()
                .primaryId(primaryId)
                .secondaryId(secondaryId)
                .reason(reason)
                .result(result)
                .tags(tags)
                .context(context)
                .payload(Optional
                        .ofNullable(payload)
                        .orElseGet(() -> Payload.builder().build()))
                .build();

        log.debug("Dispatching event {}", event);
        configuration.getEventsConsumers().forEach(consumer -> consumer.match(event));
    }
}
