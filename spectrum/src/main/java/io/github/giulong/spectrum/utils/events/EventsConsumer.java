package io.github.giulong.spectrum.utils.events;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.events.html_report.*;
import io.github.giulong.spectrum.utils.events.video.*;
import io.github.giulong.spectrum.utils.web_driver_events.TestStepsConsumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SlackConsumer.class, name = "slack"),
        @JsonSubTypes.Type(value = TestBookConsumer.class, name = "testbook"),
        @JsonSubTypes.Type(value = GenericScreenshotConsumer.class, name = "genericScreenshot"),
        @JsonSubTypes.Type(value = ExtentScreenshotConsumer.class, name = "extentScreenshot"),
        @JsonSubTypes.Type(value = ExtentTestEndConsumer.class, name = "extentTestEnd"),
        @JsonSubTypes.Type(value = DriverConsumer.class, name = "driver"),
        @JsonSubTypes.Type(value = MailConsumer.class, name = "mail"),
        @JsonSubTypes.Type(value = VideoInitConsumer.class, name = "videoInit"),
        @JsonSubTypes.Type(value = VideoDynamicInitConsumer.class, name = "videoDynamicInit"),
        @JsonSubTypes.Type(value = VideoConsumer.class, name = "video"),
        @JsonSubTypes.Type(value = VideoDynamicConsumer.class, name = "videoDynamic"),
        @JsonSubTypes.Type(value = VideoFinalizer.class, name = "videoFinalizer"),
        @JsonSubTypes.Type(value = VideoDynamicFinalizer.class, name = "videoDynamicFinalizer"),
        @JsonSubTypes.Type(value = TestStepsConsumer.class, name = "testSteps"),
        @JsonSubTypes.Type(value = LogConsumer.class, name = "log"),
        @JsonSubTypes.Type(value = VisualRegressionReferenceCreatorConsumer.class, name = "visualRegressionReferenceCreator"),
        @JsonSubTypes.Type(value = VisualRegressionCheckConsumer.class, name = "visualRegressionCheck"),
})
@Getter
@Slf4j
public abstract class EventsConsumer implements Consumer<Event> {

    @SuppressWarnings("unused")
    @JsonPropertyDescription("List of events that will be consumed")
    private List<Event> events;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Set to true to fail the test on consumer's exceptions")
    private boolean failOnError;

    public void match(final Event event) {
        final String simpleName = getClass().getSimpleName();
        final Predicate<Event> matches = e -> findMatchFor(event, e);
        final Predicate<Event> isAccepted = e -> shouldAccept(event);

        events
                .stream()
                .peek(e -> log.debug("{}: checking if should run for {}", simpleName, event))
                .filter(matches.and(isAccepted))
                .peek(e -> log.debug("{} is consuming {}", simpleName, event))
                .forEach(e -> acceptSilently(event));
    }

    protected boolean shouldAccept(final Event event) {
        return true;
    }

    boolean tagsIntersect(final Event e1, final Event e2) {
        final boolean matches = e1.getTags() != null && e2.getTags() != null &&
                e1
                        .getTags()
                        .stream()
                        .anyMatch(e2.getTags()::contains);

        log.trace("tagsIntersect: {}", matches);
        return matches;
    }

    boolean primaryAndSecondaryIdMatch(final Event e1, final Event e2) {
        final boolean matches = e1.getPrimaryId() != null && e2.getPrimaryId() != null && e1.getPrimaryId().matches(e2.getPrimaryId()) &&
                e1.getSecondaryId() != null && e2.getSecondaryId() != null && e1.getSecondaryId().matches(e2.getSecondaryId());

        log.trace("primaryAndSecondaryIdMatch: {}", matches);
        return matches;
    }

    boolean justPrimaryIdMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getPrimaryId() != null && e2.getPrimaryId() != null && e1.getPrimaryId().matches(e2.getPrimaryId())
                && e1.getSecondaryId() == null;

        log.trace("justPrimaryIdMatches: {}", matches);
        return matches;
    }

    boolean reasonMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getReason() != null && e2.getReason() != null && e1.getReason().matches(e2.getReason());

        log.trace("reasonMatches: {}", matches);
        return matches;
    }

    boolean resultMatches(final Event e1, final Event e2) {
        final boolean matches = e1.getResult() != null && e1.getResult().equals(e2.getResult());

        log.trace("resultMatches: {}", matches);
        return matches;
    }

    boolean findMatchFor(final Event e1, final Event e2) {
        return (reasonMatches(e1, e2) || resultMatches(e1, e2)) &&
                (primaryAndSecondaryIdMatch(e1, e2) || justPrimaryIdMatches(e1, e2) || tagsIntersect(e1, e2));
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    void acceptSilently(final Event event) {
        try {
            accept(event);
        } catch (Exception e) {
            log.error(String.format("%s: %s", getClass().getSimpleName(), e.getMessage()), e);

            if (failOnError) {
                throw e;
            }
        }
    }
}
