package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@JsonView(Internal.class)
public class DriverConsumer extends EventsConsumer {

    @Override
    public void consumes(final Event event) {
        final Configuration.Runtime runtime = event
                .getContext()
                .getRoot()
                .getStore(GLOBAL)
                .get(CONFIGURATION, Configuration.class)
                .getRuntime();

        runtime.getDriver().shutdown();
        runtime.getEnvironment().shutdown();
    }
}
