package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.pojos.Screenshot;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.HtmlUtils;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.pojos.Screenshot.SCREENSHOT;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ScreenshotConsumerTest {

    @Mock
    private HtmlUtils htmlUtils;

    @Mock
    private ContextManager contextManager;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private ExtensionContext context;

    @Mock
    private Screenshot screenshot;

    @Mock
    private Map<String, Screenshot> screenshots;

    @Mock
    private Video video;

    @InjectMocks
    private ScreenshotConsumer screenshotConsumer = new ScreenshotConsumer(ScreenshotConsumer.builder());

    @BeforeEach
    void beforeEach() {
        Reflections.setField("contextManager", screenshotConsumer, contextManager);
        Reflections.setField("htmlUtils", screenshotConsumer, htmlUtils);
        Reflections.setField("eventsDispatcher", screenshotConsumer, eventsDispatcher);
    }

    @Test
    @DisplayName("accept should record the screenshot")
    void accept() {
        final Frame frame = AUTO_AFTER;
        final String screenshotName = "screenshotName";

        when(video.shouldRecord(eq(frame))).thenReturn(true);
        when(webDriverEvent.getFrame()).thenReturn(frame);
        when(htmlUtils.buildScreenshotFrom(context)).thenReturn(screenshot);

        when(contextManager.getScreenshots()).thenReturn(screenshots);
        when(screenshot.getName()).thenReturn(screenshotName);

        screenshotConsumer.accept(webDriverEvent);

        verify(screenshots).put(screenshotName, screenshot);
        verify(eventsDispatcher).fire(SCREENSHOT, SCREENSHOT, Map.of(EXTENSION_CONTEXT, context, SCREENSHOT, screenshot));
        verifyNoMoreInteractions(eventsDispatcher);
    }

    @Test
    @DisplayName("accept should not record the screenshot")
    void acceptShouldNotRecord() {
        final Frame frame = AUTO_AFTER;

        when(video.shouldRecord(eq(frame))).thenReturn(false);
        when(webDriverEvent.getFrame()).thenReturn(frame);

        screenshotConsumer.accept(webDriverEvent);

        verifyNoInteractions(eventsDispatcher);
    }
}
