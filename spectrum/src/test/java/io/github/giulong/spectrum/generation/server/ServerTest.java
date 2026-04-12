package io.github.giulong.spectrum.generation.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ServerTest {

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
    @DisplayName("stop should immediately stop the http server")
    void stop() {
        server.stop();

        verify(httpServer).stop(0);
    }
}
