package io.github.giulong.spectrum.utils.events.video;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.events.EventsConsumer;

import java.nio.file.Path;

import static io.github.giulong.spectrum.enums.Result.DISABLED;

public abstract class VideoBaseConsumer extends EventsConsumer {

    protected final Configuration configuration = Configuration.getInstance();

    @Override
    protected boolean shouldAccept(final Event event) {
        return !DISABLED.equals(event.getResult()) && !configuration.getVideo().isDisabled();
    }

    protected Path getVideoPathFrom(final TestData testData) {
        return testData.getVideoPath();
    }
}
