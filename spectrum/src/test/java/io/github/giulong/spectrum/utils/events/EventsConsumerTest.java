package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventsConsumer")
class EventsConsumerTest {

    @InjectMocks
    private DummyEventsConsumer eventsConsumer;

    @DisplayName("tagsIntersect")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("tagsIntersectProvider")
    public void tagsIntersect(final Set<String> value1, final Set<String> value2, final boolean expected) {
        final Event e1 = Event.builder().tags(value1).build();
        final Event e2 = Event.builder().tags(value2).build();

        assertEquals(expected, eventsConsumer.tagsIntersect(e1, e2));
    }

    public static Stream<Arguments> tagsIntersectProvider() {
        return Stream.of(
                arguments(null, null, false),
                arguments(Set.of(TEST), null, false),
                arguments(null, Set.of(TEST), false),
                arguments(Set.of(SUITE, TEST), Set.of(TEST), true),
                arguments(Set.of(TEST), Set.of(SUITE, TEST), true)
        );
    }

    @DisplayName("primaryAndSecondaryIdMatch")
    @ParameterizedTest(name = "with value1 {0}, value2 {1}, value3 {2}, value4 {3} we expect {5}")
    @MethodSource("primaryAndSecondaryIdMatchProvider")
    public void primaryAndSecondaryIdMatch(final String value1, final String value2, final String value3, final String value4, final boolean expected) {
        final Event e1 = Event.builder().primaryId(value1).secondaryId(value2).build();
        final Event e2 = Event.builder().primaryId(value3).secondaryId(value4).build();

        assertEquals(expected, eventsConsumer.primaryAndSecondaryIdMatch(e1, e2));
    }

    public static Stream<Arguments> primaryAndSecondaryIdMatchProvider() {
        return Stream.of(
                arguments(null, null, "nope", "nope", false),
                arguments(null, "test", "nope", "nope", false),
                arguments("class", null, "nope", "nope", false),
                arguments("class", "test", null, null, false),
                arguments("class", "test", null, "nope", false),
                arguments("class", "test", "nope", null, false),
                arguments("class", "test", "nope", "nope", false),
                arguments("class", "test", "class", "nope", false),
                arguments("class", "test", "class", "test", true),
                arguments("classAAA", "test", "class.*", "test", true),
                arguments("class", "testAAA", "class", "test.*", true),
                arguments("classAAA", "testAAA", "class.*", "test.*", true)
        );
    }

    @DisplayName("justPrimaryIdMatches")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("justPrimaryIdMatchesProvider")
    public void justPrimaryIdMatches(final String value1, final String value2, final boolean expected) {
        final Event e1 = Event.builder().primaryId(value1).build();
        final Event e2 = Event.builder().primaryId(value2).build();

        assertEquals(expected, eventsConsumer.justPrimaryIdMatches(e1, e2));
    }

    public static Stream<Arguments> justPrimaryIdMatchesProvider() {
        return Stream.of(
                arguments(null, "nope", false),
                arguments("class", null, false),
                arguments("class", "nope", false),
                arguments("class", "class", true),
                arguments("classAAA", "class.*", true)
        );
    }

    @DisplayName("reasonMatches")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("reasonMatchesProvider")
    public void reasonMatches(final String value1, final String value2, final boolean expected) {
        final Event e1 = Event.builder().reason(value1).build();
        final Event e2 = Event.builder().reason(value2).build();

        assertEquals(expected, eventsConsumer.reasonMatches(e1, e2));
    }

    public static Stream<Arguments> reasonMatchesProvider() {
        return Stream.of(
                arguments(null, BEFORE, false),
                arguments(AFTER, null, false),
                arguments(AFTER, BEFORE, false),
                arguments(AFTER, AFTER, true),
                arguments("afterAAA", "after.*", true)
        );
    }

    @DisplayName("resultMatches")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("resultProvider")
    public void resultMatches(final Result value1, final Result value2, final boolean expected) {
        final Event e1 = Event.builder().result(value1).build();
        final Event e2 = Event.builder().result(value2).build();

        assertEquals(expected, eventsConsumer.resultMatches(e1, e2));
    }

    public static Stream<Arguments> resultProvider() {
        return Stream.of(
                arguments(null, FAILED, false),
                arguments(SUCCESSFUL, FAILED, false),
                arguments(SUCCESSFUL, SUCCESSFUL, true)
        );
    }

    @DisplayName("findMatchFor")
    @ParameterizedTest(name = "with event1 {0} and event2 {1} we expect {2}")
    @MethodSource("findMatchForProvider")
    public void findMatchFor(final Event e1, final Event e2, final boolean expected) {
        assertEquals(expected, eventsConsumer.findMatchFor(e1, e2));
    }

    public static Stream<Arguments> findMatchForProvider() {
        return Stream.of(
                arguments(Event.builder().build(), Event.builder().build(), false),
                arguments(Event.builder().reason(BEFORE).build(), Event.builder().reason(BEFORE).build(), false),
                arguments(Event.builder().reason(BEFORE).primaryId("class").secondaryId("test").build(), Event.builder().reason(BEFORE).primaryId("class").build(), false),
                arguments(Event.builder().reason(BEFORE).primaryId("class").secondaryId("test").build(), Event.builder().reason(BEFORE).primaryId("class").secondaryId("test").build(), true),
                arguments(Event.builder().reason(BEFORE).primaryId("class").build(), Event.builder().reason(BEFORE).primaryId("nope").build(), false),
                arguments(Event.builder().reason(BEFORE).primaryId("class").build(), Event.builder().reason(BEFORE).primaryId("class").build(), true),
                arguments(Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), Event.builder().reason(BEFORE).tags(Set.of(SUITE)).build(), false),
                arguments(Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), true),
                arguments(Event.builder().result(FAILED).build(), Event.builder().result(FAILED).build(), false),
                arguments(Event.builder().result(FAILED).primaryId("class").secondaryId("test").build(), Event.builder().result(FAILED).primaryId("class").build(), false),
                arguments(Event.builder().result(FAILED).primaryId("class").secondaryId("test").build(), Event.builder().result(FAILED).primaryId("class").secondaryId("test").build(), true),
                arguments(Event.builder().result(FAILED).primaryId("class").build(), Event.builder().result(FAILED).primaryId("nope").build(), false),
                arguments(Event.builder().result(FAILED).primaryId("class").build(), Event.builder().result(FAILED).primaryId("class").build(), true),
                arguments(Event.builder().result(FAILED).tags(Set.of(TEST)).build(), Event.builder().result(FAILED).tags(Set.of(SUITE)).build(), false),
                arguments(Event.builder().result(FAILED).tags(Set.of(TEST)).build(), Event.builder().result(FAILED).tags(Set.of(TEST)).build(), true)
        );
    }

    @Test
    @DisplayName("match should filter all the consumers listening to the provided event and let them consume it")
    public void match() {
        final Event firedEvent = mock(Event.class);
        final Event matchingEvent = mock(Event.class);
        final Event neverMatchingEvent = mock(Event.class);
        final Result result = SUCCESSFUL;
        final String className = "className";

        when(firedEvent.getResult()).thenReturn(result);
        when(matchingEvent.getResult()).thenReturn(result);
        when(firedEvent.getPrimaryId()).thenReturn(className);
        when(matchingEvent.getPrimaryId()).thenReturn(className);

        eventsConsumer.events = List.of(matchingEvent, neverMatchingEvent);
        eventsConsumer.match(firedEvent);

        // we use the getContext method in the consumes of the DummyEventsConsumer below just to verify the interaction
        verify(firedEvent).getContext();
        verify(matchingEvent, never()).getContext();    // we never consume the user-defined event (as "event"). We consume the fired event
        verify(neverMatchingEvent, never()).getContext();
    }

    @Test
    @DisplayName("consumeSilently should ignore any exception thrown when consuming the provided event")
    public void consumeSilently() {
        final String exceptionMessage = "THE STACKTRACE BELOW IS EXPECTED!!!";
        final Event event = mock(Event.class);

        when(event.getContext()).thenThrow(new RuntimeException(exceptionMessage));

        eventsConsumer.events = List.of(event);
        assertDoesNotThrow(() -> eventsConsumer.consumeSilently(event), exceptionMessage);
    }

    private static class DummyEventsConsumer extends EventsConsumer {

        public DummyEventsConsumer() {
            events = List.of(
                    Event.builder().reason(BEFORE).primaryId("class").build()
            );
        }

        @Override
        public void consumes(final Event event) {
            //noinspection ResultOfMethodCallIgnored
            event.getContext();
        }
    }
}
