package com.github.giulong.spectrum.utils.events;

import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.pojos.events.Event;

import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
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
