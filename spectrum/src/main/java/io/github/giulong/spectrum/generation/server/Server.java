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

        log.info("Server is accepting requests at {}", httpServer.getAddress());
        return this;
    }

    public void addNavigationTo(final String url) {
        log.info("Adding navigate to {}", url);
        actions.add(Action.builder().type("navigate").data(url).build());
        handler.addNavigationTo(url);

        final Action lastNavigate = handler.getLastNavigate();

        if (url.equals(lastNavigate.getData())) {
            log.error("Removing {} since it happened interacting with the page", lastNavigate);
            handler.removeLastNavigation();
        }
    }

    public void stop() {
        httpServer.stop(0);
        log.trace("Recorded actions: {}", actions);
    }
}
