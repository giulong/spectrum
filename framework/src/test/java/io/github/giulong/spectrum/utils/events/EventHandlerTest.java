package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.enums.EventReason;
import io.github.giulong.spectrum.enums.EventTag;
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

import static io.github.giulong.spectrum.enums.EventReason.AFTER;
import static io.github.giulong.spectrum.enums.EventReason.BEFORE;
import static io.github.giulong.spectrum.enums.EventTag.SUITE;
import static io.github.giulong.spectrum.enums.EventTag.TEST;
import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventHandler")
class EventHandlerTest {

    @InjectMocks
    private DummyEventHandler eventHandler;

    @DisplayName("tagsIntersect")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("tagsIntersectProvider")
    public void tagsIntersect(final Set<EventTag> value1, final Set<EventTag> value2, final boolean expected) {
        final Event e1 = Event.builder().tags(value1).build();
        final Event e2 = Event.builder().tags(value2).build();

        assertEquals(expected, eventHandler.tagsIntersect(e1, e2));
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

    @DisplayName("classNameAndTestNameMatches")
    @ParameterizedTest(name = "with value1 {0}, value2 {1}, value3 {2}, value4 {3} we expect {5}")
    @MethodSource("classNameAndTestNameMatchesProvider")
    public void classNameAndTestNameMatches(final String value1, final String value2, final String value3, final String value4, final boolean expected) {
        final Event e1 = Event.builder().className(value1).testName(value2).build();
        final Event e2 = Event.builder().className(value3).testName(value4).build();

        assertEquals(expected, eventHandler.classNameAndTestNameMatches(e1, e2));
    }

    public static Stream<Arguments> classNameAndTestNameMatchesProvider() {
        return Stream.of(
                arguments(null, null, "nope", "nope", false),
                arguments(null, "test", "nope", "nope", false),
                arguments("class", null, "nope", "nope", false),
                arguments("class", "test", "nope", "nope", false),
                arguments("class", "test", "class", "nope", false),
                arguments("class", "test", "class", "test", true)
        );
    }

    @DisplayName("justClassNameMatches")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("justClassNameMatchesProvider")
    public void justClassNameMatches(final String value1, final String value2, final boolean expected) {
        final Event e1 = Event.builder().className(value1).build();
        final Event e2 = Event.builder().className(value2).build();

        assertEquals(expected, eventHandler.justClassNameMatches(e1, e2));
    }

    public static Stream<Arguments> justClassNameMatchesProvider() {
        return Stream.of(
                arguments(null, "nope", false),
                arguments("class", "nope", false),
                arguments("class", "class", true)
        );
    }

    @DisplayName("reasonMatches")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("reasonMatchesProvider")
    public void reasonMatches(final EventReason value1, final EventReason value2, final boolean expected) {
        final Event e1 = Event.builder().reason(value1).build();
        final Event e2 = Event.builder().reason(value2).build();

        assertEquals(expected, eventHandler.reasonMatches(e1, e2));
    }

    public static Stream<Arguments> reasonMatchesProvider() {
        return Stream.of(
                arguments(null, BEFORE, false),
                arguments(AFTER, BEFORE, false),
                arguments(AFTER, AFTER, true)
        );
    }

    @DisplayName("resultMatches")
    @ParameterizedTest(name = "with value1 {0} and value2 {1} we expect {2}")
    @MethodSource("resultProvider")
    public void resultMatches(final Result value1, final Result value2, final boolean expected) {
        final Event e1 = Event.builder().result(value1).build();
        final Event e2 = Event.builder().result(value2).build();

        assertEquals(expected, eventHandler.resultMatches(e1, e2));
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
        assertEquals(expected, eventHandler.findMatchFor(e1, e2));
    }

    public static Stream<Arguments> findMatchForProvider() {
        return Stream.of(
                arguments(Event.builder().build(), Event.builder().build(), false),
                arguments(Event.builder().reason(BEFORE).build(), Event.builder().reason(BEFORE).build(), false),
                arguments(Event.builder().reason(BEFORE).className("class").testName("test").build(), Event.builder().reason(BEFORE).className("class").build(), false),
                arguments(Event.builder().reason(BEFORE).className("class").testName("test").build(), Event.builder().reason(BEFORE).className("class").testName("test").build(), true),
                arguments(Event.builder().reason(BEFORE).className("class").build(), Event.builder().reason(BEFORE).className("nope").build(), false),
                arguments(Event.builder().reason(BEFORE).className("class").build(), Event.builder().reason(BEFORE).className("class").build(), true),
                arguments(Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), Event.builder().reason(BEFORE).tags(Set.of(SUITE)).build(), false),
                arguments(Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), Event.builder().reason(BEFORE).tags(Set.of(TEST)).build(), true),
                arguments(Event.builder().result(FAILED).build(), Event.builder().result(FAILED).build(), false),
                arguments(Event.builder().result(FAILED).className("class").testName("test").build(), Event.builder().result(FAILED).className("class").build(), false),
                arguments(Event.builder().result(FAILED).className("class").testName("test").build(), Event.builder().result(FAILED).className("class").testName("test").build(), true),
                arguments(Event.builder().result(FAILED).className("class").build(), Event.builder().result(FAILED).className("nope").build(), false),
                arguments(Event.builder().result(FAILED).className("class").build(), Event.builder().result(FAILED).className("class").build(), true),
                arguments(Event.builder().result(FAILED).tags(Set.of(TEST)).build(), Event.builder().result(FAILED).tags(Set.of(SUITE)).build(), false),
                arguments(Event.builder().result(FAILED).tags(Set.of(TEST)).build(), Event.builder().result(FAILED).tags(Set.of(TEST)).build(), true)
        );
    }

    @Test
    @DisplayName("match should filter all the handlers listening to the provided event and let them handle it")
    public void match() {
        final Event firedEvent = mock(Event.class);
        final Event matchingEvent = mock(Event.class);
        final Event neverMatchingEvent = mock(Event.class);
        final Result result = SUCCESSFUL;
        final String className = "className";

        when(firedEvent.getResult()).thenReturn(result);
        when(matchingEvent.getResult()).thenReturn(result);
        when(firedEvent.getClassName()).thenReturn(className);
        when(matchingEvent.getClassName()).thenReturn(className);

        eventHandler.handles = List.of(matchingEvent, neverMatchingEvent);
        eventHandler.match(firedEvent);

        // we use the getContext method in the handle of the DummyEventHandler below just to verify the interaction
        verify(firedEvent).getContext();
        verify(matchingEvent, never()).getContext();    // we never handle the user-defined event (as "handle"). We handle the fired event
        verify(neverMatchingEvent, never()).getContext();
    }

    @Test
    @DisplayName("handleSilently should ignore any exception thrown when handling the provided event")
    public void handleSilently() {
        final String exceptionMessage = "THE STACKTRACE BELOW IS EXPECTED!!!";
        final Event event = mock(Event.class);

        when(event.getContext()).thenThrow(new RuntimeException(exceptionMessage));

        eventHandler.handles = List.of(event);
        assertDoesNotThrow(() -> eventHandler.handleSilently(event), exceptionMessage);
    }

    private static class DummyEventHandler extends EventHandler {

        public DummyEventHandler() {
            handles = List.of(
                    Event.builder().reason(BEFORE).className("class").build()
            );
        }

        @Override
        public void handle(final Event event) {
            //noinspection ResultOfMethodCallIgnored
            event.getContext();
        }
    }
}
