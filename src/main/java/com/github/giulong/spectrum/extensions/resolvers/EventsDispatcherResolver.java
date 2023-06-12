package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.SpectrumSessionListener;
import com.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class EventsDispatcherResolver extends TypeBasedParameterResolver<EventsDispatcher> {

    public static final String EVENTS_DISPATCHER = "eventsDispatcher";

    @Override
    public EventsDispatcher resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(EVENTS_DISPATCHER, e -> {
            log.debug("Resolving {}", EVENTS_DISPATCHER);

            final EventsDispatcher eventsDispatcher = SpectrumSessionListener.getEventsDispatcher();
            rootStore.put(EVENTS_DISPATCHER, eventsDispatcher);
            return eventsDispatcher;
        }, EventsDispatcher.class);
    }
}
