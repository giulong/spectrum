package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.EventTag;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.AFTER;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventsDispatcher")
class EventsDispatcherTest {

    private MockedStatic<Event> eventMockedStatic;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private Event.EventBuilder eventBuilder;

    @Mock
    private Event event;

    @Mock
    private EventHandler handler1;

    @Mock
    private EventHandler handler2;

    private EventsDispatcher eventsDispatcher;

    @BeforeEach
    public void beforeEach() {
        eventMockedStatic = mockStatic(Event.class);
        eventsDispatcher = EventsDispatcher
                .builder()
                .handlers(List.of(handler1, handler2))
                .build();
    }

    @AfterEach
    public void afterEach() {
        eventMockedStatic.close();
    }

    @Test
    @DisplayName("fire should build an event with the provided reason and tags and call match on every handler")
    public void fire() {
        final String reason = AFTER;
        final Set<EventTag> tags = Set.of();

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(reason, tags);

        verify(handler1).match(event);
        verify(handler2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with the provided primaryId and reason and call match on every handler")
    public void firePrimaryIdAndReason() {
        final String reason = AFTER;
        final String primaryId = "primaryId";

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(primaryId)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(null)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(null)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(primaryId, reason);

        verify(handler1).match(event);
        verify(handler2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with the provided primaryId, secondaryId and reason and call match on every handler")
    public void firePrimaryIdAndSecondaryIdAndReason() {
        final String reason = AFTER;
        final String primaryId = "primaryId";
        final String secondaryId = "secondaryId";

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(primaryId)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(secondaryId)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(null)).thenReturn(eventBuilder);
        when(eventBuilder.tags(null)).thenReturn(eventBuilder);
        when(eventBuilder.context(null)).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(primaryId, secondaryId, reason);

        verify(handler1).match(event);
        verify(handler2).match(event);
    }

    @Test
    @DisplayName("fire should build an event with all the provided parameters and call match on every handler")
    public void fireAllParams() {
        final String className = "className";
        final String testName = "testName";
        final String reason = AFTER;
        final Result result = SUCCESSFUL;
        final Set<EventTag> tags = Set.of();

        when(Event.builder()).thenReturn(eventBuilder);
        when(eventBuilder.primaryId(className)).thenReturn(eventBuilder);
        when(eventBuilder.secondaryId(testName)).thenReturn(eventBuilder);
        when(eventBuilder.reason(reason)).thenReturn(eventBuilder);
        when(eventBuilder.result(result)).thenReturn(eventBuilder);
        when(eventBuilder.tags(tags)).thenReturn(eventBuilder);
        when(eventBuilder.context(extensionContext)).thenReturn(eventBuilder);
        when(eventBuilder.build()).thenReturn(event);

        eventsDispatcher.fire(className, testName, reason, result, tags, extensionContext);

        verify(handler1).match(event);
        verify(handler2).match(event);
    }
}