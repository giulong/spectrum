package io.github.giulong.spectrum.extensions.watchers;

import io.github.giulong.spectrum.SpectrumSessionListener;
import io.github.giulong.spectrum.enums.Result;
import org.junit.jupiter.api.extension.*;

import java.util.Optional;
import java.util.Set;

import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;

public class EventsWatcher implements TestWatcher, BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    @Override
    public void beforeAll(final ExtensionContext context) {
        notifyClass(context, BEFORE, null, Set.of(CLASS));
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        notifyTest(context, BEFORE, null, Set.of(TEST));
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        notifyClass(context, AFTER, null, Set.of(CLASS));
    }

    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        notifyTest(context, AFTER, DISABLED, Set.of(TEST));
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        notifyTest(context, AFTER, SUCCESSFUL, Set.of(TEST));
    }

    @Override
    public void testAborted(final ExtensionContext context, final Throwable throwable) {
        notifyTest(context, AFTER, ABORTED, Set.of(TEST));
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable exception) {
        notifyTest(context, AFTER, FAILED, Set.of(TEST));
    }

    public void notifyClass(final ExtensionContext context, final String reason, final Result result, final Set<String> tags) {
        final String className = context.getDisplayName();

        SpectrumSessionListener.getEventsDispatcher().fire(className, null, reason, result, tags, context);
    }

    public void notifyTest(final ExtensionContext context, final String reason, final Result result, final Set<String> tags) {
        final String className = context.getParent().orElse(context.getRoot()).getDisplayName();
        final String testName = context.getDisplayName();

        SpectrumSessionListener.getEventsDispatcher().fire(className, testName, reason, result, tags, context);
    }
}
