package io.github.giulong.spectrum.extensions.watchers;

import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Optional;
import java.util.Set;

import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;
import static org.mockito.Mockito.*;

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
    void beforeEach() {
        Reflections.setField("eventsDispatcher", eventsWatcher, eventsDispatcher);
        eventsDispatcherMockedStatic = mockStatic(EventsDispatcher.class);
    }

    @AfterEach
    void afterEach() {
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

    @TestFactory
    @SuppressWarnings("unused")
    private void testFactoryMethod() {
    }

    @Test
    @DisplayName("beforeAll should dispatch an event")
    void testBeforeAll() {
        notifyClassStubs();
        eventsWatcher.beforeAll(extensionContext);
        verify(eventsDispatcher).fire(className, null, BEFORE, null, Set.of(CLASS), extensionContext);
    }

    @Test
    @DisplayName("beforeEach should dispatch an event")
    void testBeforeEach() {
        notifyTestStubs();
        eventsWatcher.beforeEach(extensionContext);
        verify(eventsDispatcher).fire(className, displayName, BEFORE, null, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("afterEach should dispatch an event only for methods annotated with TestFactory")
    void testAfterEachTestFactory() throws NoSuchMethodException {
        notifyTestStubs();

        when(extensionContext.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod("testFactoryMethod"));

        eventsWatcher.afterEach(extensionContext);
        verify(eventsDispatcher).fire(className, displayName, AFTER, SUCCESSFUL, Set.of(TEST_FACTORY), extensionContext);
    }

    @Test
    @DisplayName("afterEach should dispatch no event for methods not annotated with TestFactory")
    void testAfterEach() throws NoSuchMethodException {
        when(extensionContext.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod("testAfterEach"));

        eventsWatcher.afterEach(extensionContext);
        verifyNoInteractions(eventsDispatcher);
    }

    @Test
    @DisplayName("afterAll should dispatch an event")
    void testAfterAll() {
        notifyClassStubs();
        eventsWatcher.afterAll(extensionContext);
        verify(eventsDispatcher).fire(className, null, AFTER, null, Set.of(CLASS), extensionContext);
    }

    @Test
    @DisplayName("testDisabled should dispatch an event")
    void testDisabled() {
        notifyTestStubs();
        eventsWatcher.testDisabled(extensionContext, Optional.of("reason"));
        verify(eventsDispatcher).fire(className, displayName, AFTER, DISABLED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testSuccessful should dispatch an event")
    void testSuccessful() {
        notifyTestStubs();
        eventsWatcher.testSuccessful(extensionContext);
        verify(eventsDispatcher).fire(className, displayName, AFTER, SUCCESSFUL, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testAborted should dispatch an event")
    void testAborted() {
        notifyTestStubs();
        eventsWatcher.testAborted(extensionContext, new RuntimeException());
        verify(eventsDispatcher).fire(className, displayName, AFTER, ABORTED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testFailed should dispatch an event")
    void testFailed() {
        notifyTestStubs();
        eventsWatcher.testFailed(extensionContext, new RuntimeException());
        verify(eventsDispatcher).fire(className, displayName, AFTER, FAILED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("notifyClass should dispatch an event with the className taken from the context and no test name")
    void testNotifyClass() {
        final String reason = BEFORE;
        final Result result = SUCCESSFUL;
        final Set<String> tags = Set.of();

        notifyClassStubs();
        eventsWatcher.notifyClass(extensionContext, reason, result, tags);
        verify(eventsDispatcher).fire(className, null, reason, result, tags, extensionContext);
    }

    @Test
    @DisplayName("notifyTest should dispatch an event with the className and testName taken from the context")
    void testNotifyTest() {
        final String reason = BEFORE;
        final Result result = SUCCESSFUL;
        final Set<String> tags = Set.of();

        notifyTestStubs();
        eventsWatcher.notifyTest(extensionContext, reason, result, tags);
        verify(eventsDispatcher).fire(className, displayName, reason, result, tags, extensionContext);
    }
}
