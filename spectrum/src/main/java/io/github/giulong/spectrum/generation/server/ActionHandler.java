package io.github.giulong.spectrum.generation.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.github.giulong.spectrum.generation.server.actions.Action;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class ActionHandler implements HttpHandler {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final List<String> urls = new ArrayList<>();

    @Builder.Default
    private Action lastNavigate = Action.builder().build();;
    private List<Action> actions;
    private int currentUrlIndex;

    @Override
    public void handle(final HttpExchange exchange) {
        try {
            final String payload = new Scanner(exchange.getRequestBody()).useDelimiter("\\Z").next();
            final Action action = objectMapper.readValue(payload, Action.class);

            log.info("Received {}", action);
            if (action.getType().equals("navigate")) {
                lastNavigate = action;
                urls.subList(0, currentUrlIndex++);
                addNavigationTo(action.getData());
            } else if (action.getType().equals("traverse")) {
                if (action.getData().equals(urls.get(urls.size() - 2))) {
                    log.debug("Back navigation");
                    actions.add(Action.builder().type("back").data(action.getData()).build());
                    currentUrlIndex--;
                } else {
                    log.debug("Forward navigation");
                    actions.add(Action.builder().type("forward").data(action.getData()).build());
                    currentUrlIndex++;
                }
            } else {
                actions.add(action);
            }

            exchange.sendResponseHeaders(200, -1);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void addNavigationTo(final String url) {
        log.info("Adding url '{}'", url);
        urls.add(url);
    }

    void removeLastNavigation() {
        actions.remove(lastNavigate);
        urls.removeLast();
        currentUrlIndex--;
        lastNavigate = Action.builder().build();
    }
}
