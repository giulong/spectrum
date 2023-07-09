package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class BrowserHandler extends EventHandler {

    public void handle(final Event event) {
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
