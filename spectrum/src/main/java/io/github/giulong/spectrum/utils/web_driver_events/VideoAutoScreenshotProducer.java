package io.github.giulong.spectrum.utils.web_driver_events;

import static org.openqa.selenium.OutputType.BYTES;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.pojos.events.Event.Payload;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.TakesScreenshot;

@Slf4j
@SuperBuilder
public class VideoAutoScreenshotProducer extends WebDriverEventConsumer {

    public static final String SCREENSHOT = "screenshot";

    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();

    private Video video;
    private TakesScreenshot driver;
    private ExtensionContext context;

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final Frame frame = webDriverEvent.getFrame();

        if (video.shouldRecord(frame)) {
            final Payload payload = Payload
                    .builder()
                    .screenshot(driver.getScreenshotAs(BYTES))
                    .takesScreenshot(driver)
                    .build();

            eventsDispatcher.fire(frame.getValue(), SCREENSHOT, context, payload);
            log.trace("Recording frame {} for event '{}'", frame, webDriverEvent.getMessage());

            return;
        }

        log.trace("Not recording frame {}", frame);
    }
}
