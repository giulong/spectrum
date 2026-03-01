package io.github.giulong.spectrum.generation.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.util.List;

import com.sun.net.httpserver.HttpServer;

import io.github.giulong.spectrum.generation.server.actions.Action;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ServerTest {

    private final String url = "url";

    @Mock
    private Action lastNavigate;

    @Mock
    private List<Action> actions;

    @Mock
    private ActionHandler handler;

    @Mock
    private HttpServer httpServer;

    @Mock
    private InetSocketAddress inetSocketAddress;

    @InjectMocks
    private Server server;

    @Test
    @DisplayName("start should create the root context")
    void start() {
        when(httpServer.getAddress()).thenReturn(inetSocketAddress);

        assertEquals(server, server.start());

        verify(httpServer).createContext("/", handler);
        verify(httpServer).start();
    }

    @Test
    @DisplayName("addNavigationTo should build the navigate action and remove the last navigate if their urls are equal")
    void addNavigationToRemoveLast() {
        when(handler.getLastNavigate()).thenReturn(lastNavigate);
        when(lastNavigate.getData()).thenReturn(url);

        server.addNavigationTo(url);

        verify(actions).add(Action.builder().type("navigate").data(url).build());
        verify(handler).addNavigationTo(url);
        verify(handler).removeLastNavigation();
    }

    @Test
    @DisplayName("addNavigationTo should build the navigate action")
    void addNavigationTo() {
        when(handler.getLastNavigate()).thenReturn(lastNavigate);
        when(lastNavigate.getData()).thenReturn("not matching");

        server.addNavigationTo(url);

        verify(actions).add(Action.builder().type("navigate").data(url).build());
        verify(handler).addNavigationTo(url);
        verify(handler, never()).removeLastNavigation();
    }

    @Test
    @DisplayName("stop should immediately stop the http server")
    void stop() {
        server.stop();

        verify(httpServer).stop(0);
    }
}
