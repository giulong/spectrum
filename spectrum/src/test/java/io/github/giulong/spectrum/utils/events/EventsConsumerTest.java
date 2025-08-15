package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Reflections;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class EventsConsumerTest {

    private static final String PRIMARY_ID = "primaryId";
    private static final String EXCEPTION_MESSAGE = "THE STACKTRACE BELOW IS EXPECTED!!!";

    @InjectMocks
    private DummyEventsConsumer eventsConsumer;

    @InjectMocks
    private DummyThrowingEventsConsumer throwingEventsConsumer;

    @Test
    @DisplayName("the default shouldAccept should just return true")
    void shouldAccept() {
        final Event event = mock();

        assertTrue(eventsConsumer.shouldAccept(event));
        verifyNoInteractions(event);
    }

    @DisplayName("tagsIntersect")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("tagsIntersectProvider")
    void tagsIntersect(final Set<String> value1, final Set<String> value2, final boolean expected) {
        final Event e1 = Event.builder().tags(value1).build();
        final Event e2 = Event.builder().tags(value2).build();

        assertEquals(expected, eventsConsumer.tagsIntersect(e1, e2));
    }

    static Stream<Arguments> tagsIntersectProvider() {
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
    void primaryAndSecondaryIdMatch(final String value1, final String value2, final String value3, final String value4, final boolean expected) {
        final Event e1 = Event.builder().primaryId(value1).secondaryId(value2).build();
        final Event e2 = Event.builder().primaryId(value3).secondaryId(value4).build();

        assertEquals(expected, eventsConsumer.primaryAndSecondaryIdMatch(e1, e2));
    }

    static Stream<Arguments> primaryAndSecondaryIdMatchProvider() {
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
    void justPrimaryIdMatches(final String value1, final String value2, final boolean expected) {
        final Event e1 = Event.builder().primaryId(value1).build();
        final Event e2 = Event.builder().primaryId(value2).build();

        assertEquals(expected, eventsConsumer.justPrimaryIdMatches(e1, e2));
    }

    static Stream<Arguments> justPrimaryIdMatchesProvider() {
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
    void reasonMatches(final String value1, final String value2, final boolean expected) {
        final Event e1 = Event.builder().reason(value1).build();
        final Event e2 = Event.builder().reason(value2).build();

        assertEquals(expected, eventsConsumer.reasonMatches(e1, e2));
    }

    static Stream<Arguments> reasonMatchesProvider() {
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
    void resultMatches(final Result value1, final Result value2, final boolean expected) {
        final Event e1 = Event.builder().result(value1).build();
        final Event e2 = Event.builder().result(value2).build();

        assertEquals(expected, eventsConsumer.resultMatches(e1, e2));
    }

    static Stream<Arguments> resultProvider() {
        return Stream.of(
                arguments(null, FAILED, false),
                arguments(SUCCESSFUL, FAILED, false),
                arguments(SUCCESSFUL, SUCCESSFUL, true)
        );
    }

    @DisplayName("findMatchFor")
    @ParameterizedTest(name = "with event1 {0} and event2 {1} we expect {2}")
    @MethodSource("findMatchForProvider")
    void findMatchFor(final Event e1, final Event e2, final boolean expected) {
        assertEquals(expected, eventsConsumer.findMatchFor(e1, e2));
    }

    static Stream<Arguments> findMatchForProvider() {
        return Stream.of(
                arguments(Event.builder().build(), Event.builder().build(), false),
                arguments(Event.builder().reason(BEFORE).build(), Event.builder().reason(BEFORE).build(), false),
                arguments(Event.builder().reason(BEFORE).primaryId("class").secondaryId("test").build(), Event.builder().reason(BEFORE).primaryId("class").build(), false),
                arguments(Event.builder().reason(BEFORE).primaryId("class").secondaryId("test").build(),
                        Event.builder().reason(BEFORE).primaryId("class").secondaryId("test").build(), true),
                arguments(Event.builder().reason(BEFORE).primaryId("class").build(), Event.builder().reason(BEFORE).primaryId("nope").build(), false),
                arguments(Event.builder().reason(BEFORE).primaryId("class").build(), Event.builder().reason(BEFORE).primaryId("class").build(), true),
                arguments(Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), Event.builder().reason(BEFORE).tags(Set.of(SUITE)).build(), false),
                arguments(Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), true),
                arguments(Event.builder().result(FAILED).build(), Event.builder().result(FAILED).build(), false),
                arguments(Event.builder().result(FAILED).primaryId("class").secondaryId("test").build(), Event.builder().result(FAILED).primaryId("class").build(), false),
                arguments(Event.builder().result(FAILED).primaryId("class").secondaryId("test").build(),
                        Event.builder().result(FAILED).primaryId("class").secondaryId("test").build(), true),
                arguments(Event.builder().result(FAILED).primaryId("class").build(), Event.builder().result(FAILED).primaryId("nope").build(), false),
                arguments(Event.builder().result(FAILED).primaryId("class").build(), Event.builder().result(FAILED).primaryId("class").build(), true),
                arguments(Event.builder().result(FAILED).tags(Set.of(TEST)).build(), Event.builder().result(FAILED).tags(Set.of(SUITE)).build(), false),
                arguments(Event.builder().result(FAILED).tags(Set.of(TEST)).build(), Event.builder().result(FAILED).tags(Set.of(TEST)).build(), true)
        );
    }

    @Test
    @DisplayName("match should filter all the consumers listening to the provided event and let them consume it")
    void match() {
        final Event firedEvent = mock(Event.class);
        final Event matchingEvent = mock(Event.class);
        final Event neverMatchingEvent = mock(Event.class);
        final Result result = SUCCESSFUL;
        final String className = "className";

        when(firedEvent.getResult()).thenReturn(result);
        when(matchingEvent.getResult()).thenReturn(result);
        when(firedEvent.getPrimaryId()).thenReturn(className);
        when(matchingEvent.getPrimaryId()).thenReturn(className);

        Reflections.setField("events", eventsConsumer, List.of(matchingEvent, neverMatchingEvent));
        eventsConsumer.match(firedEvent);

        assertEquals(Set.of(firedEvent), eventsConsumer.getAcceptedEvents());
    }

    @Test
    @DisplayName("match should call findMatchFor and shouldAccept in order")
    void matchOrder() {
        final Event event = mock(Event.class);

        Reflections.setField("events", throwingEventsConsumer, List.of(event));

        when(event.getReason()).thenReturn(BEFORE);
        when(event.getPrimaryId()).thenReturn(PRIMARY_ID);

        throwingEventsConsumer.match(event);

        // since the event should is not accepted, to prove the call order,
        // we check that findMatchFor called the stubs above but the event has not been added to the accepted events set
        assertEquals(Set.of(event), throwingEventsConsumer.getShouldAcceptEvents());
        assertEquals(Set.of(), throwingEventsConsumer.getAcceptedEvents());
    }

    @Test
    @DisplayName("acceptSilently should ignore any exception thrown when consuming the provided event")
    void acceptSilently() {
        final String exceptionMessage = "THE STACKTRACE BELOW IS EXPECTED!!!";
        final Event event = mock(Event.class);

        Reflections.setField("events", throwingEventsConsumer, List.of(event));
        assertDoesNotThrow(() -> throwingEventsConsumer.acceptSilently(event), exceptionMessage);
    }

    @Test
    @DisplayName("acceptSilently should rethrow the exception if failOnError is true")
    void acceptSilentlyThrow() {
        final Event event = mock(Event.class);

        Reflections.setField("failOnError", throwingEventsConsumer, true);
        Reflections.setField("events", throwingEventsConsumer, List.of(event));

        assertThrowsExactly(RuntimeException.class, () -> throwingEventsConsumer.acceptSilently(event), EXCEPTION_MESSAGE);
    }

    @Getter
    private static class DummyEventsConsumer extends EventsConsumer {

        private final Set<Event> shouldAcceptEvents = new HashSet<>();
        private final Set<Event> acceptedEvents = new HashSet<>();

        DummyEventsConsumer() {
            Reflections.setField("events", this, List.of(
                    Event.builder().reason(BEFORE).primaryId(PRIMARY_ID).build()
            ));
        }

        @Override
        protected boolean shouldAccept(final Event event) {
            shouldAcceptEvents.add(event);
            return super.shouldAccept(event);
        }

        @Override
        public void accept(final Event event) {
            acceptedEvents.add(event);
        }
    }

    @Getter
    private static class DummyThrowingEventsConsumer extends EventsConsumer {

        private final Set<Event> shouldAcceptEvents = new HashSet<>();
        private final Set<Event> acceptedEvents = new HashSet<>();

        DummyThrowingEventsConsumer() {
            Reflections.setField("events", this, List.of(
                    Event.builder().reason(BEFORE).primaryId(PRIMARY_ID).build()
            ));
        }

        @Override
        protected boolean shouldAccept(final Event event) {
            shouldAcceptEvents.add(event);
            return false;
        }

        @Override
        public void accept(final Event event) {
            acceptedEvents.add(event);
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
    }
}
