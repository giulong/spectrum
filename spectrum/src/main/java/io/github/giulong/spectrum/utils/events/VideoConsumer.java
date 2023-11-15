package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.video.VideoEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.VideoEncoderResolver.VIDEO_ENCODER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@JsonView(Internal.class)
public class VideoConsumer extends EventsConsumer {

    public void consumes(final Event event) {
        final ExtensionContext.Store store = event.getContext().getStore(GLOBAL);
        final Configuration.Extent extent = store.get(CONFIGURATION, Configuration.class).getExtent();
        if (extent.getVideo().getRecording().isDisabled()) {
            log.debug("Video is disabled. Returning");
            return;
        }

        store.get(VIDEO_ENCODER, VideoEncoder.class).done();
    }
}