package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;

import static io.github.giulong.spectrum.enums.Result.DISABLED;

@Slf4j
@JsonView(Internal.class)
public class DriverConsumer extends EventsConsumer {

    private final Configuration configuration = Configuration.getInstance();

    @Override
    public void accept(final Event event) {
        if (event.getResult().equals(DISABLED)) {
            log.debug("Test is skipped. Returning");
            return;
        }

        final Configuration.Runtime runtime = configuration.getRuntime();

        if (!configuration.getDrivers().isKeepOpen()) {
            runtime.getDriver().shutdown();
        }

        runtime.getEnvironment().shutdown();
    }
}
