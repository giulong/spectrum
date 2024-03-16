package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.video.ScreenshotWatcher;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.video.VideoEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotWatcherResolver.SCREENSHOT_WATCHER;
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
    private Video video;

    @Mock
    private ScreenshotWatcher screenshotWatcher;

    @Mock
    private VideoEncoder videoEncoder;

    @InjectMocks
    private VideoConsumer videoConsumer;

    @Test
    @DisplayName("accept should notify the VideoEncoder that the test is done")
    public void accept() {
        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(store.get(SCREENSHOT_WATCHER, ScreenshotWatcher.class)).thenReturn(screenshotWatcher);
        when(store.get(VIDEO_ENCODER, VideoEncoder.class)).thenReturn(videoEncoder);

        videoConsumer.accept(event);

        verify(screenshotWatcher).done();
        verify(videoEncoder).done();
    }

    @Test
    @DisplayName("accept shouldn't do nothing when video recording is disabled")
    public void acceptDisabled() {
        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(true);

        videoConsumer.accept(event);
        verify(screenshotWatcher, never()).done();
        verify(videoEncoder, never()).done();
    }
}
