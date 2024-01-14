package io.github.giulong.spectrum.extensions.watchers;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.utils.ReflectionUtils;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventsWatcher")
class EventsWatcherTest {

    private static MockedStatic<EventsDispatcher> eventsDispatcherMockedStatic;

    private final String className = "className";
    private final String displayName = "displayName";

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @InjectMocks
    private EventsWatcher eventsWatcher;

    @BeforeEach
    public void beforeEach() {
        ReflectionUtils.setField("eventsDispatcher", eventsWatcher, eventsDispatcher);
        eventsDispatcherMockedStatic = mockStatic(EventsDispatcher.class);
    }

    @AfterEach
    public void afterEach() {
        eventsDispatcherMockedStatic.close();
    }

    private void notifyClassStubs() {
        when(EventsDispatcher.getInstance()).thenReturn(eventsDispatcher);
        when(extensionContext.getDisplayName()).thenReturn(className);
    }

    private void notifyTestStubs() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(EventsDispatcher.getInstance()).thenReturn(eventsDispatcher);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
    }

    @Test
    @DisplayName("beforeAll should dispatch an event")
    public void testBeforeAll() {
        notifyClassStubs();
        eventsWatcher.beforeAll(extensionContext);
        verify(eventsDispatcher).fire(className, null, BEFORE, null, Set.of(CLASS), extensionContext);
    }

    @Test
    @DisplayName("beforeEach should dispatch an event")
    public void testBeforeEach() {
        notifyTestStubs();
        eventsWatcher.beforeEach(extensionContext);
        verify(eventsDispatcher).fire(className, displayName, BEFORE, null, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("afterAll should dispatch an event")
    public void testAfterAll() {
        notifyClassStubs();
        eventsWatcher.afterAll(extensionContext);
        verify(eventsDispatcher).fire(className, null, AFTER, null, Set.of(CLASS), extensionContext);
    }

    @Test
    @DisplayName("testDisabled should dispatch an event")
    public void testDisabled() {
        notifyTestStubs();
        eventsWatcher.testDisabled(extensionContext, Optional.of("reason"));
        verify(eventsDispatcher).fire(className, displayName, AFTER, DISABLED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testSuccessful should dispatch an event")
    public void testSuccessful() {
        notifyTestStubs();
        eventsWatcher.testSuccessful(extensionContext);
        verify(eventsDispatcher).fire(className, displayName, AFTER, SUCCESSFUL, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testAborted should dispatch an event")
    public void testAborted() {
        notifyTestStubs();
        eventsWatcher.testAborted(extensionContext, new RuntimeException());
        verify(eventsDispatcher).fire(className, displayName, AFTER, ABORTED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testFailed should dispatch an event")
    public void testFailed() {
        notifyTestStubs();
        eventsWatcher.testFailed(extensionContext, new RuntimeException());
        verify(eventsDispatcher).fire(className, displayName, AFTER, FAILED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("notifyClass should dispatch an event with the className taken from the context and no test name")
    public void testNotifyClass() {
        final String reason = BEFORE;
        final Result result = SUCCESSFUL;
        final Set<String> tags = Set.of();

        notifyClassStubs();
        eventsWatcher.notifyClass(extensionContext, reason, result, tags);
        verify(eventsDispatcher).fire(className, null, reason, result, tags, extensionContext);
    }

    @Test
    @DisplayName("notifyTest should dispatch an event with the className and testName taken from the context")
    public void testNotifyTest() {
        final String reason = BEFORE;
        final Result result = SUCCESSFUL;
        final Set<String> tags = Set.of();

        notifyTestStubs();
        eventsWatcher.notifyTest(extensionContext, reason, result, tags);
        verify(eventsDispatcher).fire(className, displayName, reason, result, tags, extensionContext);
    }
}
