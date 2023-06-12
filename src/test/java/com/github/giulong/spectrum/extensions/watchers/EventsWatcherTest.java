package com.github.giulong.spectrum.extensions.watchers;

import com.github.giulong.spectrum.SpectrumSessionListener;
import com.github.giulong.spectrum.enums.EventReason;
import com.github.giulong.spectrum.enums.EventTag;
import com.github.giulong.spectrum.enums.Result;
import com.github.giulong.spectrum.utils.events.EventsDispatcher;
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

import static com.github.giulong.spectrum.enums.EventReason.AFTER;
import static com.github.giulong.spectrum.enums.EventReason.BEFORE;
import static com.github.giulong.spectrum.enums.EventTag.CLASS;
import static com.github.giulong.spectrum.enums.EventTag.TEST;
import static com.github.giulong.spectrum.enums.Result.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventsWatcher")
class EventsWatcherTest {

    private static MockedStatic<SpectrumSessionListener> spectrumSessionListenerMockedStatic;

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
        spectrumSessionListenerMockedStatic = mockStatic(SpectrumSessionListener.class);
    }

    @AfterEach
    public void afterEach() {
        spectrumSessionListenerMockedStatic.close();
    }

    private void notifyStubs() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(SpectrumSessionListener.getEventsDispatcher()).thenReturn(eventsDispatcher);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
    }

    @Test
    @DisplayName("beforeAll should dispatch an event")
    public void testBeforeAll() {
        notifyStubs();
        eventsWatcher.beforeAll(extensionContext);
        verify(eventsDispatcher).dispatch(className, displayName, BEFORE, null, Set.of(CLASS), extensionContext);
    }

    @Test
    @DisplayName("beforeEach should dispatch an event")
    public void testBeforeEach() {
        notifyStubs();
        eventsWatcher.beforeEach(extensionContext);
        verify(eventsDispatcher).dispatch(className, displayName, BEFORE, null, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("afterAll should dispatch an event")
    public void testAfterAll() {
        notifyStubs();
        eventsWatcher.afterAll(extensionContext);
        verify(eventsDispatcher).dispatch(className, displayName, AFTER, null, Set.of(CLASS), extensionContext);
    }

    @Test
    @DisplayName("testDisabled should dispatch an event")
    public void testDisabled() {
        notifyStubs();
        eventsWatcher.testDisabled(extensionContext, Optional.of("reason"));
        verify(eventsDispatcher).dispatch(className, displayName, AFTER, DISABLED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testSuccessful should dispatch an event")
    public void testSuccessful() {
        notifyStubs();
        eventsWatcher.testSuccessful(extensionContext);
        verify(eventsDispatcher).dispatch(className, displayName, AFTER, SUCCESSFUL, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testAborted should dispatch an event")
    public void testAborted() {
        notifyStubs();
        eventsWatcher.testAborted(extensionContext, new RuntimeException());
        verify(eventsDispatcher).dispatch(className, displayName, AFTER, ABORTED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("testFailed should dispatch an event")
    public void testFailed() {
        notifyStubs();
        eventsWatcher.testFailed(extensionContext, new RuntimeException());
        verify(eventsDispatcher).dispatch(className, displayName, AFTER, FAILED, Set.of(TEST), extensionContext);
    }

    @Test
    @DisplayName("notify should dispatch an event with the className and testName taken from the context")
    public void testNotify() {
        final EventReason reason = BEFORE;
        final Result result = SUCCESSFUL;
        final Set<EventTag> tags = Set.of();

        notifyStubs();
        eventsWatcher.notify(extensionContext, reason, result, tags);
        verify(eventsDispatcher).dispatch(className, displayName, reason, result, tags, extensionContext);
    }
}
