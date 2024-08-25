package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;

@JsonView(Internal.class)
public class DriverConsumer extends EventsConsumer {

    private final Configuration configuration = Configuration.getInstance();

    @Override
    public void accept(final Event event) {
        final Configuration.Runtime runtime = configuration.getRuntime();

        runtime.getDriver().shutdown();
        runtime.getEnvironment().shutdown();
    }
}
