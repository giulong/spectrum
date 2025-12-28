package io.github.giulong.spectrum.utils.web_driver_events;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.OutputType.BYTES;

import java.util.Map;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.TakesScreenshot;

class VideoAutoScreenshotProducerTest {

    @Mock
    private TakesScreenshot driver;

    @MockSingleton
    @SuppressWarnings("unused")
    private EventsDispatcher eventsDispatcher;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private ExtensionContext context;

    @Mock
    private Video video;

    @InjectMocks
    private VideoAutoScreenshotProducer videoAutoScreenshotProducer = new VideoAutoScreenshotProducer(VideoAutoScreenshotProducer.builder());

    @Test
    @DisplayName("accept should record the screenshot")
    void accept() {
        final Frame frame = AUTO_AFTER;
        final byte[] bytes = new byte[]{1, 2, 3};

        when(video.shouldRecord(eq(frame))).thenReturn(true);
        when(webDriverEvent.getFrame()).thenReturn(frame);

        when(driver.getScreenshotAs(BYTES)).thenReturn(bytes);

        videoAutoScreenshotProducer.accept(webDriverEvent);

        verify(eventsDispatcher).fire(AUTO_AFTER.getValue(), SCREENSHOT, context, Map.of(SCREENSHOT, bytes, "takesScreenshot", driver));
        verifyNoMoreInteractions(eventsDispatcher);
    }

    @Test
    @DisplayName("accept should not record the screenshot")
    void acceptShouldNotRecord() {
        final Frame frame = AUTO_AFTER;

        when(video.shouldRecord(eq(frame))).thenReturn(false);
        when(webDriverEvent.getFrame()).thenReturn(frame);

        videoAutoScreenshotProducer.accept(webDriverEvent);

        verifyNoInteractions(eventsDispatcher);
    }
}
