package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.pojos.Screenshot;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.HtmlUtils;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.pojos.Screenshot.SCREENSHOT;

@Slf4j
@SuperBuilder
public class ScreenshotConsumer extends WebDriverEventConsumer {

    private final ContextManager contextManager = ContextManager.getInstance();
    private final HtmlUtils htmlUtils = HtmlUtils.getInstance();
    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();

    private Video video;
    private ExtensionContext context;

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final Frame frame = webDriverEvent.getFrame();

        if (video.shouldRecord(frame)) {
            final Screenshot screenshot = htmlUtils.buildScreenshotFrom(context);

            contextManager.getScreenshots().put(screenshot.getName(), screenshot);
            eventsDispatcher.fire(SCREENSHOT, SCREENSHOT, Map.of(EXTENSION_CONTEXT, context, SCREENSHOT, screenshot));
            log.trace("Recording frame {} for event '{}'", frame, webDriverEvent.getMessage());

            return;
        }

        log.trace("Not recording frame {}", frame);
    }
}
