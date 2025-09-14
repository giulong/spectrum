package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

import static com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;

@Slf4j
@JsonView(Internal.class)
public class ExtentScreenshotConsumer extends ScreenshotConsumer {

    @Override
    public void accept(final Event event) {
        log.debug("Adding screenshot to the extent report");
        this.frameNumber = configuration.getVideo().getAndIncrementFrameNumberFor(testData, MANUAL);

        final Path path = fileUtils.createTempFile("screenshot", ".png");
        final Map<String, Object> payload = event.getPayload();
        final String message = (String) payload.get("message");
        final Status status = (Status) payload.get("status");
        final String tag = htmlUtils.buildFrameTagFor(frameNumber, message, testData, "screenshot-message");

        addScreenshot(path, status, tag, createScreenCaptureFromPath(path.toString()).build());
    }
}
