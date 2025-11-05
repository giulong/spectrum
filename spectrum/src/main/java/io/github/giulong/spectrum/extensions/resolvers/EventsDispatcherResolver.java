package io.github.giulong.spectrum.extensions.resolvers;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import io.github.giulong.spectrum.utils.events.EventsDispatcher;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class EventsDispatcherResolver extends TypeBasedParameterResolver<EventsDispatcher> {

    public static final String EVENTS_DISPATCHER = "eventsDispatcher";

    @Override
    public EventsDispatcher resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        return context.getRoot().getStore(GLOBAL).computeIfAbsent(EVENTS_DISPATCHER, e -> {
            log.debug("Resolving {}", EVENTS_DISPATCHER);

            return EventsDispatcher.getInstance();
        }, EventsDispatcher.class);
    }
}
