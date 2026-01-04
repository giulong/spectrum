package io.github.giulong.spectrum.utils.events.html_report;

import static com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.pojos.events.Event.Payload;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonView(Internal.class)
public class ExtentScreenshotConsumer extends ScreenshotConsumer {

    @Override
    public void accept(final Event event) {
        log.debug("Adding screenshot to the extent report");

        final int frameNumber = configuration.getVideo().getAndIncrementFrameNumberFor(testData, MANUAL);
        final Path path = fileUtils.createTempFile("screenshot", ".png");
        final Payload payload = event.getPayload();
        final String tag = htmlUtils.buildFrameTagFor(frameNumber, payload.getMessage(), testData, "screenshot-message");

        currentNode.log(payload.getStatus(), tag, createScreenCaptureFromPath(path.toString()).build());
        addScreenshot(path);
    }
}
