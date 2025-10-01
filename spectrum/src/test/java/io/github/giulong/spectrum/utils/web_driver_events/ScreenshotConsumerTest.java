package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
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
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static io.github.giulong.spectrum.utils.web_driver_events.ScreenshotConsumer.SCREENSHOT;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class ScreenshotConsumerTest {

    @Mock
    private HtmlUtils htmlUtils;

    @Mock
    private ExtensionContext.Store store;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver driver;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private ExtensionContext context;

    @Mock
    private Video video;

    @InjectMocks
    private ScreenshotConsumer screenshotConsumer = new ScreenshotConsumer(ScreenshotConsumer.builder());

    @BeforeEach
    void beforeEach() {
        Reflections.setField("htmlUtils", screenshotConsumer, htmlUtils);
        Reflections.setField("eventsDispatcher", screenshotConsumer, eventsDispatcher);
    }

    @Test
    @DisplayName("accept should record the screenshot")
    void accept() {
        final Frame frame = AUTO_AFTER;
        final byte[] bytes = new byte[]{1, 2, 3};

        when(video.shouldRecord(eq(frame))).thenReturn(true);
        when(webDriverEvent.getFrame()).thenReturn(frame);

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(DRIVER, WebDriver.class)).thenReturn(driver);
        when(((TakesScreenshot) driver).getScreenshotAs(BYTES)).thenReturn(bytes);

        screenshotConsumer.accept(webDriverEvent);

        verify(eventsDispatcher).fire(SCREENSHOT, SCREENSHOT, context, Map.of(SCREENSHOT, bytes));
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
