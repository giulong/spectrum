package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonView(Internal.class)
public class VideoResetConsumer extends EventsConsumer {

    private final Configuration configuration = Configuration.getInstance();

    @Override
    public void accept(final Event event) {
        configuration.getVideo().resetFrameNumber();
    }
}
