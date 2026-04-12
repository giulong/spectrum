package io.github.giulong.spectrum.generation.server;

import static io.github.giulong.spectrum.generation.server.actions.TraverseAction.Status.BACK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import io.github.giulong.spectrum.generation.server.actions.Action;
import io.github.giulong.spectrum.generation.server.actions.ElementNavigateAction;
import io.github.giulong.spectrum.generation.server.actions.NavigateAction;
import io.github.giulong.spectrum.generation.server.actions.TraverseAction;
import io.github.giulong.spectrum.utils.Reflections;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

@Slf4j
class ActionHandlerTest {

    private final String payload = "payload";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private NavigateAction lastNavigate;

    @Mock
    private List<NavigateAction> navigateActions;

    @Mock
    private Action action;

    @Mock
    private List<Action> actions;

    @Mock
    private HttpExchange exchange;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private ActionHandler handler;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("actions", handler, actions);
        Reflections.setField("navigateActions", handler, navigateActions);
        Reflections.setField("lastNavigate", handler, lastNavigate);
    }

    @Test
    @DisplayName("handle should parse the payload and manage the corresponding action")
    void handle() {
        handleWithStubsFor(action);

        verify(actions).add(action);
    }

    @Test
    @DisplayName("handle should manage the element navigate action")
    void handleElementNavigate() {
        final int size = 123;
        final ElementNavigateAction elementNavigateAction = mock();

        when(navigateActions.size()).thenReturn(size);

        handleWithStubsFor(elementNavigateAction);

        verify(actions, never()).add(elementNavigateAction);
        verify(navigateActions).add(elementNavigateAction);

        final int actualNavigateIndex = Reflections.getFieldValue("navigateIndex", handler);
        assertEquals(size, actualNavigateIndex);

        final NavigateAction actualLastNavigate = Reflections.getFieldValue("lastNavigate", handler);
        assertEquals(elementNavigateAction, actualLastNavigate);
    }

    @DisplayName("handle should manage the traverse action, clamping the navigation index as a valid index of navigateActions")
    @ParameterizedTest(name = "with navigateIndex = {0}")
    @CsvSource({"10,9", "-10,0", "999,122"})
    void handleTraverse(final int navigationIndexToClamp, final int clampedNavigationIndex) {
        final int size = 123;
        final TraverseAction traverseAction = mock();

        Reflections.setField("navigateIndex", handler, navigationIndexToClamp);
        when(navigateActions.size()).thenReturn(size);
        when(traverseAction.getRelativeIndexFrom(navigationIndexToClamp, navigateActions)).thenReturn(BACK.getIndex());

        handleWithStubsFor(traverseAction);

        final int actualNavigateIndex = Reflections.getFieldValue("navigateIndex", handler);

        assertEquals(clampedNavigationIndex, actualNavigateIndex);
        verify(actions).add(traverseAction);

        final NavigateAction actualLastNavigate = Reflections.getFieldValue("lastNavigate", handler);
        assertEquals(traverseAction, actualLastNavigate);
    }

    @Test
    @DisplayName("handle should manage the navigate action, ignoring it if the last one was an ElementNavigateAction")
    void handleNavigateIgnore() {
        final NavigateAction navigateAction = mock();
        final ElementNavigateAction elementNavigateAction = mock();
        Reflections.setField("lastNavigate", handler, elementNavigateAction);

        handleWithStubsFor(navigateAction);

        verifyNoMoreInteractions(navigateAction);
    }

    @Test
    @DisplayName("handle should manage the navigate action, subListing navigateActions to the current navigation index")
    void handleNavigate() {
        final String url = "url";
        final int size = 123;
        final int navigateIndex = 1;
        final NavigateAction navigateAction = mock();
        final List<NavigateAction> elementsToBeCleared = mock();

        Reflections.setField("navigateIndex", handler, navigateIndex);

        when(navigateActions.size()).thenReturn(size);
        when(navigateAction.getUrl()).thenReturn(url);
        when(navigateActions.subList(navigateIndex, size)).thenReturn(elementsToBeCleared);
        when(lastNavigate.getUrl()).thenReturn("nope");

        handleWithStubsFor(navigateAction);

        verify(navigateActions).add(navigateAction);
        verify(actions).add(navigateAction);
        verify(elementsToBeCleared).clear();

        final NavigateAction actualLastNavigate = Reflections.getFieldValue("lastNavigate", handler);
        assertEquals(navigateAction, actualLastNavigate);

        final int actualNavigateIndex = Reflections.getFieldValue("navigateIndex", handler);
        assertEquals(size, actualNavigateIndex);

    }

    @Test
    @DisplayName("handle should manage the navigate action")
    void handleNavigateSameUrlAsLastOne() {
        final String url = "url";
        final int size = 123;
        final NavigateAction navigateAction = mock();

        when(navigateActions.size()).thenReturn(size);
        when(navigateAction.getUrl()).thenReturn(url);
        when(lastNavigate.getUrl()).thenReturn(url);

        handleWithStubsFor(navigateAction);

        verify(navigateActions).add(navigateAction);
        verify(actions).add(navigateAction);

        final NavigateAction actualLastNavigate = Reflections.getFieldValue("lastNavigate", handler);
        assertEquals(navigateAction, actualLastNavigate);

        final int actualNavigateIndex = Reflections.getFieldValue("navigateIndex", handler);
        assertEquals(size, actualNavigateIndex);

        verify(navigateActions, never()).subList(anyInt(), anyInt());
    }

    @Test
    @DisplayName("handle should log and rethrow IO exceptions")
    void handleThrows() {
        try (MockedConstruction<Scanner> ignored = mockConstruction((mock, context) -> {
            assertEquals(inputStream, context.arguments().getFirst());

            when(mock.useDelimiter("\\Z")).thenReturn(mock);
            when(mock.next()).thenReturn(payload);
        })) {
            when(exchange.getRequestBody()).thenReturn(inputStream);

            log.error("THE STACKTRACE BELOW IS EXPECTED!!!");
            assertThrows(RuntimeException.class, () -> handler.handle(exchange));
        }
    }

    @SneakyThrows
    private void handleWithStubsFor(final Action specificAction) {
        Reflections.setField("objectMapper", handler, objectMapper);

        try (MockedConstruction<Scanner> ignored = mockConstruction((mock, context) -> {
            assertEquals(inputStream, context.arguments().getFirst());

            when(mock.useDelimiter("\\Z")).thenReturn(mock);
            when(mock.next()).thenReturn(payload);
        })) {
            when(exchange.getRequestBody()).thenReturn(inputStream);
            when(objectMapper.readValue(payload, Action.class)).thenReturn(specificAction);

            handler.handle(exchange);

            verify(exchange).sendResponseHeaders(200, -1);
        }
    }
}
