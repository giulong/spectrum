package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@JsonView(Views.Internal.class)
public class BrowserConsumer extends EventsConsumer {

    public void consumes(final Event event) {
        event
                .getContext()
                .getRoot()
                .getStore(GLOBAL)
                .get(CONFIGURATION, Configuration.class)
                .getRuntime()
                .getBrowser()
                .shutdown();
    }
}
