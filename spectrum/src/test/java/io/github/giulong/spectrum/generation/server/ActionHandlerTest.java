package io.github.giulong.spectrum.generation.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import io.github.giulong.spectrum.generation.server.actions.Action;
import io.github.giulong.spectrum.utils.Reflections;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

@Slf4j
class ActionHandlerTest {

    private final String payload = "payload";
    private final String url = "url";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private List<String> urls;

    @Mock
    private Action lastNavigate;

    @Mock
    private Action action;

    @Mock
    private List<Action> actions;

    @Mock
    private HttpExchange exchange;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private ActionHandler handler = new ActionHandler(lastNavigate, actions, 0);

    @Test
    @DisplayName("handle should parse the payload and manage the corresponding action")
    void handle() {
        when(action.getType()).thenReturn("type");

        handleStubs();

        verify(actions).add(action);

        assertEquals(lastNavigate, handler.getLastNavigate());
        assertEquals(0, handler.getCurrentUrlIndex());
        verify(urls, never()).subList(0, 0);
    }

    @Test
    @DisplayName("handle should manage the navigate action")
    void handleNavigate() {
        when(action.getType()).thenReturn("navigate");
        when(action.getData()).thenReturn(url);

        handleStubs();

        verify(actions, never()).add(action);

        assertEquals(action, handler.getLastNavigate());
        assertEquals(1, handler.getCurrentUrlIndex());
        verify(urls).subList(0, 0);
        verify(urls).add(url);
    }

    @Test
    @DisplayName("handle should manage the traverse back action")
    void handleTraverseBack() {
        when(action.getType()).thenReturn("traverse");
        when(action.getData()).thenReturn(url);

        when(urls.size()).thenReturn(123);
        when(urls.get(121)).thenReturn(url);

        handleStubs();

        verify(actions, never()).add(action);

        assertEquals(lastNavigate, handler.getLastNavigate());
        assertEquals(-1, handler.getCurrentUrlIndex());
        verify(actions).add(Action.builder().type("back").data(url).build());
    }

    @Test
    @DisplayName("handle should manage the traverse forward action")
    void handleTraverseForward() {
        when(action.getType()).thenReturn("traverse");
        when(action.getData()).thenReturn(url);

        when(urls.size()).thenReturn(123);
        when(urls.get(121)).thenReturn("nope");

        handleStubs();

        verify(actions, never()).add(action);

        assertEquals(lastNavigate, handler.getLastNavigate());
        assertEquals(1, handler.getCurrentUrlIndex());
        verify(actions).add(Action.builder().type("forward").data(url).build());
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

    @Test
    @DisplayName("addNavigationTo should add the provided url to the list")
    void addNavigationTo() {
        assertTrue(handler.getUrls().isEmpty());

        handler.addNavigationTo(url);

        assertEquals(List.of(url), handler.getUrls());
    }

    @Test
    @DisplayName("removeLastNavigation should remove the last navigate action from the list, updating urls, index, and resetting the last navigate")
    void removeLastNavigation() {
        Reflections.setField("urls", handler, urls);

        handler.getActions().add(lastNavigate);
        handler.getUrls().add(url);

        assertEquals(0, handler.getCurrentUrlIndex());
        handler.removeLastNavigation();

        verify(actions).remove(lastNavigate);
        verify(urls).removeLast();
        assertEquals(-1, handler.getCurrentUrlIndex());
        assertEquals(Action.builder().build(), handler.getLastNavigate());
    }

    @SneakyThrows
    private void handleStubs() {
        Reflections.setField("objectMapper", handler, objectMapper);
        Reflections.setField("urls", handler, urls);

        try (MockedConstruction<Scanner> ignored = mockConstruction((mock, context) -> {
            assertEquals(inputStream, context.arguments().getFirst());

            when(mock.useDelimiter("\\Z")).thenReturn(mock);
            when(mock.next()).thenReturn(payload);
        })) {
            when(exchange.getRequestBody()).thenReturn(inputStream);
            when(objectMapper.readValue(payload, Action.class)).thenReturn(action);

            assertEquals(0, handler.getCurrentUrlIndex());

            handler.handle(exchange);

            verify(exchange).sendResponseHeaders(200, -1);
        }
    }
}
