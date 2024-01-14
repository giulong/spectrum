package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.EventsDispatcherResolver.EVENTS_DISPATCHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventsDispatcherResolver")
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
    public void beforeEach() {
        eventsDispatcherMockedStatic = mockStatic(EventsDispatcher.class);
    }

    @AfterEach
    public void afterEach() {
        eventsDispatcherMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of EventsDispatcher")
    public void testResolveParameter() {
        when(EventsDispatcher.getInstance()).thenReturn(eventsDispatcher);

        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        final EventsDispatcherResolver eventsDispatcherResolver = new EventsDispatcherResolver();
        eventsDispatcherResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).getOrComputeIfAbsent(eq(EVENTS_DISPATCHER), functionArgumentCaptor.capture(), eq(EventsDispatcher.class));
        Function<String, EventsDispatcher> function = functionArgumentCaptor.getValue();
        final EventsDispatcher actual = function.apply("value");

        verify(rootStore).put(EVENTS_DISPATCHER, eventsDispatcher);
        assertEquals(eventsDispatcher, actual);
    }
}
