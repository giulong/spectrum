package io.github.giulong.spectrum.utils.events;

import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.AFTER;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.BEFORE;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.SUITE;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.pojos.events.Event.Payload;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Summary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class EventsDispatcherTest {

    private final String primaryId = "primaryId";

    private MockedStatic<Event> eventMockedStatic;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Summary summary;

    @Mock
    private Payload payload;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private Event.EventBuilder eventBuilder;

    @Mock
    private Event event;

    @Mock
    private EventsConsumer consumer1;

    @Mock
    private EventsConsumer consumer2;

    @InjectMocks
    private EventsDispatcher eventsDispatcher;

    @BeforeEach
    void beforeEach() {
        eventMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        eventMockedStatic.close();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(EventsDispatcher.getInstance(), EventsDispatcher.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should fire the before suite event")
    void sessionOpened() {
        final Set<String> tags = Set.of(SUITE);

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(BEFORE)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.sessionOpened();
    }

    @Test
    @DisplayName("sessionClosed should fire the after suite event")
    void sessionClosed() {
        final Set<String> tags = Set.of(SUITE);
        final Result result = SUCCESSFUL;

        when(configuration.getSummary()).thenReturn(summary);
        when(summary.toResult()).thenReturn(result);

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(AFTER)).thenReturn(eventBuilder);
        when(eventBuilder.result(result)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.sessionClosed();
    }

    @Test
    @DisplayName("fire should build an event with the provided reason and tags and call match on every consumer")
    void fire() {
        final String reason = AFTER;
        final Set<String> tags = Set.of();

        when(configuration.getEventsConsumers()).thenReturn(List.of(consumer1, consumer2));

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(reason, tags);

        verify(consumer1).match(event);
        verify(consumer2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with the provided reason, tags and result and call match on every consumer")
    void fireReasonTagsResult() {
        final String reason = AFTER;
        final Set<String> tags = Set.of();
        final Result result = SUCCESSFUL;

        when(configuration.getEventsConsumers()).thenReturn(List.of(consumer1, consumer2));

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(result)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(reason, tags, result);

        verify(consumer1).match(event);
        verify(consumer2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with the provided primaryId, reason, and payload and call match on every consumer")
    void firePrimaryIdReasonPayload() {
        final String reason = AFTER;

        when(configuration.getEventsConsumers()).thenReturn(List.of(consumer1, consumer2));

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(primaryId)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(null)).thenReturn(eventBuilder);
        when(eventBuilder.context(extensionContext)).thenReturn(eventBuilder);
        when(eventBuilder.payload(payload)).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(primaryId, reason, extensionContext, payload);

        verify(consumer1).match(event);
        verify(consumer2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with the provided primaryId and reason and call match on every consumer")
    void firePrimaryIdAndReason() {
        final String reason = AFTER;

        when(configuration.getEventsConsumers()).thenReturn(List.of(consumer1, consumer2));

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(primaryId)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(null)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(primaryId, reason);

        verify(consumer1).match(event);
        verify(consumer2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with the provided primaryId, secondaryId and reason and call match on every consumer")
    void firePrimaryIdAndSecondaryIdAndReason() {
        final String reason = AFTER;
        final String secondaryId = "secondaryId";

        when(configuration.getEventsConsumers()).thenReturn(List.of(consumer1, consumer2));

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(primaryId)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(secondaryId)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(null)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(primaryId, secondaryId, reason);

        verify(consumer1).match(event);
        verify(consumer2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with all the provided parameters and call match on every consumer")
    void fireAllParams() {
        final String className = "className";
        final String testName = "testName";
        final String reason = AFTER;
        final Result result = SUCCESSFUL;
        final Set<String> tags = Set.of();

        when(configuration.getEventsConsumers()).thenReturn(List.of(consumer1, consumer2));

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(className)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(testName)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(result)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(extensionContext)).thenReturn(eventBuilder);
        when(eventBuilder.payload(Payload.builder().build())).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(className, testName, reason, result, tags, extensionContext);

        verify(consumer1).match(event);
        verify(consumer2).match(event);
    }
}
