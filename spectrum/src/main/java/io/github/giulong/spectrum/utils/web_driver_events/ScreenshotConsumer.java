package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.openqa.selenium.OutputType.BYTES;

@Slf4j
@SuperBuilder
public class ScreenshotConsumer extends WebDriverEventConsumer {

    public static final String SCREENSHOT = "screenshot";
    public static final String MANUAL_SCREENSHOT = "manual-screenshot";
    public static final String AUTO_SCREENSHOT = "auto-screenshot";

    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();

    private Video video;
    private ExtensionContext context;

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final Frame frame = webDriverEvent.getFrame();

        if (video.shouldRecord(frame)) {
            final byte[] screenshot = ((TakesScreenshot) context.getStore(GLOBAL).get(DRIVER, WebDriver.class)).getScreenshotAs(BYTES);

            eventsDispatcher.fire(AUTO_SCREENSHOT, SCREENSHOT, Map.of(EXTENSION_CONTEXT, context, SCREENSHOT, screenshot));
            log.trace("Recording frame {} for event '{}'", frame, webDriverEvent.getMessage());

            return;
        }

        log.trace("Not recording frame {}", frame);
    }
}
