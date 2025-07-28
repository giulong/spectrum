package io.github.giulong.spectrum.extensions.watchers;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.*;

import java.util.Optional;
import java.util.Set;

import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;

public class EventsWatcher implements TestWatcher, BeforeTestExecutionCallback, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();

    @Override
    public void beforeAll(final ExtensionContext context) {
        notifyClass(context, BEFORE, null, Set.of(CLASS));
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        notifyTest(context, BEFORE, null, Set.of(TEST));
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        notifyTest(context, BEFORE_EXECUTION, null, Set.of(TEST));

        if (isTestFactory(context)) {
            notifyTest(context, BEFORE_EXECUTION, null, Set.of(TEST_FACTORY));
        }
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        if (isTestFactory(context)) {
            notifyTest(context, AFTER, SUCCESSFUL, Set.of(TEST_FACTORY));
        }
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

    boolean isTestFactory(final ExtensionContext context) {
        return context.getRequiredTestMethod().isAnnotationPresent(TestFactory.class);
    }

    void notifyClass(final ExtensionContext context, final String reason, final Result result, final Set<String> tags) {
        final String className = context.getDisplayName();

        eventsDispatcher.fire(className, null, reason, result, tags, context);
    }

    void notifyTest(final ExtensionContext context, final String reason, final Result result, final Set<String> tags) {
        final String className = context.getParent().orElse(context.getRoot()).getDisplayName();
        final String testName = context.getDisplayName();

        eventsDispatcher.fire(className, testName, reason, result, tags, context);
    }
}
