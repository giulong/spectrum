package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.video.Recording;
import io.github.giulong.spectrum.utils.video.VideoEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.VideoEncoderResolver.VIDEO_ENCODER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoConsumer")
class VideoConsumerTest {

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Event event;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private Configuration.Extent.Video video;

    @Mock
    private Recording recording;

    @Mock
    private VideoEncoder videoEncoder;

    @InjectMocks
    private VideoConsumer videoConsumer;

    @Test
    @DisplayName("consumes should notify the VideoEncoder that the test is done")
    public void consumes() {
        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getVideo()).thenReturn(video);
        when(video.getRecording()).thenReturn(recording);
        when(recording.isDisabled()).thenReturn(false);
        when(store.get(VIDEO_ENCODER, VideoEncoder.class)).thenReturn(videoEncoder);

        videoConsumer.consumes(event);
        verify(videoEncoder).done();
    }

    @Test
    @DisplayName("consumes shouldn't do nothing when video recording is disabled")
    public void consumesDisabled() {
        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getVideo()).thenReturn(video);
        when(video.getRecording()).thenReturn(recording);
        when(recording.isDisabled()).thenReturn(true);

        videoConsumer.consumes(event);
        verify(videoEncoder, never()).done();
    }
}