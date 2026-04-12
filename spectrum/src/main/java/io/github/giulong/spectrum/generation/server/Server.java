package io.github.giulong.spectrum.generation.server;

import java.util.List;

import com.sun.net.httpserver.HttpServer;

import io.github.giulong.spectrum.generation.server.actions.Action;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class Server {

    private List<Action> actions;
    private ActionHandler handler;
    private HttpServer httpServer;

    public Server start() {
        httpServer.createContext("/", handler);
        httpServer.start();

        System.setProperty("record_server_port", String.valueOf(httpServer.getAddress().getPort()));
        log.info("Accepting requests at {}", httpServer.getAddress());
        return this;
    }

    public void stop() {
        httpServer.stop(0);
        log.trace("Recorded actions: {}", actions);
    }
}
