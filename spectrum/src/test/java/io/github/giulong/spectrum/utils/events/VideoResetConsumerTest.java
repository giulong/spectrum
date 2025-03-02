package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class VideoResetConsumerTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private Event event;

    @InjectMocks
    private VideoResetConsumer videoResetConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", videoResetConsumer, configuration);
    }

    @Test
    @DisplayName("accept should just reset the video frame number")
    void accept() {
        when(configuration.getVideo()).thenReturn(video);

        videoResetConsumer.accept(event);

        verify(video).resetFrameNumber();
        verifyNoInteractions(event);
    }
}
