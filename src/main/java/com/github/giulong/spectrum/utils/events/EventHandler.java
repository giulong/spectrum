package com.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.giulong.spectrum.pojos.events.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.stream.Collectors.toSet;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SlackHandler.class, name = "slack"),
        @JsonSubTypes.Type(value = TestBookHandler.class, name = "testbook"),
        @JsonSubTypes.Type(value = ExtentTestHandler.class, name = "extentTest"),
        @JsonSubTypes.Type(value = WebDriverHandler.class, name = "webDriver"),
})
@Getter
@Slf4j
public abstract class EventHandler {

    protected List<Event> handles;

    public abstract void handle(Event event);

    protected boolean tagsIntersect(final Event e1, final Event e2) {
        final boolean matches = e1.getTags() != null && e2.getTags() != null &&
                !e1
                        .getTags()
                        .stream()
                        .filter(e2.getTags()::contains)
                        .collect(toSet())
                        .isEmpty();

        log.trace("tagsIntersect: {}", matches);
        return matches;
    }

    protected boolean classNameAndTestNameMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getClassName() != null && e1.getClassName().equals(e2.getClassName()) &&
                e1.getTestName() != null && e1.getTestName().equals(e2.getTestName());

        log.trace("classNameAndTestNameMatches: {}", matches);
        return matches;
    }

    protected boolean justClassNameMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getClassName() != null && e1.getClassName().equals(e2.getClassName()) && e1.getTestName() == null;

        log.trace("justClassNameMatches: {}", matches);
        return matches;
    }

    protected boolean reasonMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getReason() != null && e1.getReason().equals(e2.getReason());

        log.trace("reasonMatches: {}", matches);
        return matches;
    }

    protected boolean resultMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getResult() != null && e1.getResult().equals(e2.getResult());

        log.trace("resultMatches: {}", matches);
        return matches;
    }

    protected boolean findMatchFor(Event e1, Event e2) {
        return (reasonMatches(e1, e2) || resultMatches(e1, e2)) &&
                (classNameAndTestNameMatches(e1, e2) || justClassNameMatches(e1, e2) || tagsIntersect(e1, e2));
    }

    public void match(final Event event) {
        handles
                .stream()
                .peek(h -> log.trace("{} matchers for {}", getClass().getSimpleName(), event))
                .filter(h -> findMatchFor(event, h))
                .peek(h -> log.debug("{} is handling {}", getClass().getSimpleName(), event))
                .forEach(h -> handle(event));
    }
}
