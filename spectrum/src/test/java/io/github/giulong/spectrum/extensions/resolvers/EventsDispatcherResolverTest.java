package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.EventsDispatcherResolver.EVENTS_DISPATCHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

import java.util.function.Function;

import io.github.giulong.spectrum.utils.events.EventsDispatcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class EventsDispatcherResolverTest {

    private MockedStatic<EventsDispatcher> eventsDispatcherMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Captor
    private ArgumentCaptor<Function<String, EventsDispatcher>> functionArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        eventsDispatcherMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        eventsDispatcherMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of EventsDispatcher")
    void testResolveParameter() {
        when(EventsDispatcher.getInstance()).thenReturn(eventsDispatcher);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        final EventsDispatcherResolver eventsDispatcherResolver = new EventsDispatcherResolver();
        eventsDispatcherResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).computeIfAbsent(eq(EVENTS_DISPATCHER), functionArgumentCaptor.capture(), eq(EventsDispatcher.class));
        Function<String, EventsDispatcher> function = functionArgumentCaptor.getValue();
        final EventsDispatcher actual = function.apply("value");

        assertEquals(eventsDispatcher, actual);
    }
}
