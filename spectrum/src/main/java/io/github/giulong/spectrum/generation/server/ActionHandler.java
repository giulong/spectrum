package io.github.giulong.spectrum.generation.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.github.giulong.spectrum.generation.server.actions.Action;
import io.github.giulong.spectrum.generation.server.actions.ElementNavigateAction;
import io.github.giulong.spectrum.generation.server.actions.NavigateAction;
import io.github.giulong.spectrum.generation.server.actions.TraverseAction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionHandler implements HttpHandler {

    private final ObjectMapper objectMapper;
    private final List<Action> actions;
    private final List<NavigateAction> navigateActions;

    private int navigateIndex;
    private NavigateAction lastNavigate;

    public ActionHandler(final List<Action> actions) {
        this.actions = actions;
        this.objectMapper = JsonMapper.builder().build();
        this.navigateActions = new ArrayList<>();
        this.lastNavigate = new NavigateAction();
    }

    @Override
    @SuppressWarnings("checkstyle:IllegalCatch")
    public void handle(final HttpExchange exchange) {
        try {
            final String payload = new Scanner(exchange.getRequestBody()).useDelimiter("\\Z").next();
            final Action action = objectMapper.readValue(payload, Action.class);

            log.info("Received {}", action);

            switch (action) {
                case ElementNavigateAction elementNavigateAction -> registerNavigateAction(elementNavigateAction);
                case TraverseAction traverseAction -> {
                    navigateIndex = Math.clamp(navigateIndex + traverseAction.getRelativeIndexFrom(navigateIndex, navigateActions), 0, navigateActions.size() - 1);
                    lastNavigate = traverseAction;
                    actions.add(traverseAction);
                }
                case NavigateAction navigateAction -> {
                    if (lastNavigate instanceof ElementNavigateAction) {
                        log.debug("Ignoring {} since it happened interacting with the page", lastNavigate);
                    } else {
                        if (!navigateAction.getUrl().equals(lastNavigate.getUrl())) {
                            navigateActions.subList(navigateIndex, navigateActions.size()).clear();
                        }

                        actions.add(action);
                    }

                    registerNavigateAction(navigateAction);
                }
                default -> actions.add(action);
            }

            exchange.sendResponseHeaders(200, -1);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void registerNavigateAction(final NavigateAction navigateAction) {
        navigateIndex = navigateActions.size();
        lastNavigate = navigateAction;
        navigateActions.add(navigateAction);
    }
}
