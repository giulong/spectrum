package io.github.giulong.spectrum.extensions.watchers;

import io.github.giulong.spectrum.SpectrumSessionListener;
import io.github.giulong.spectrum.enums.EventTag;
import io.github.giulong.spectrum.enums.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

import java.util.Optional;
import java.util.Set;

import static io.github.giulong.spectrum.enums.EventTag.CLASS;
import static io.github.giulong.spectrum.enums.EventTag.TEST;
import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.AFTER;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.BEFORE;

@Slf4j
public class EventsWatcher implements TestWatcher, BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    @Override
    public void beforeAll(final ExtensionContext context) {
        notify(context, BEFORE, null, Set.of(CLASS));
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        notify(context, BEFORE, null, Set.of(TEST));
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        notify(context, AFTER, null, Set.of(CLASS));
    }

    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        notify(context, AFTER, DISABLED, Set.of(TEST));
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        notify(context, AFTER, SUCCESSFUL, Set.of(TEST));
    }

    @Override
    public void testAborted(final ExtensionContext context, final Throwable throwable) {
        notify(context, AFTER, ABORTED, Set.of(TEST));
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable exception) {
        notify(context, AFTER, FAILED, Set.of(TEST));
    }

    public void notify(final ExtensionContext context, final String reason, final Result result, final Set<EventTag> tags) {
        final String className = context.getParent().orElse(context.getRoot()).getDisplayName();
        final String testName = context.getDisplayName();

        SpectrumSessionListener.getEventsDispatcher().fire(className, testName, reason, result, tags, context);
    }
}