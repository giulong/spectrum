package io.github.giulong.spectrum.utils.web_driver_events;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.HtmlUtils;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.Status.WARNING;
import static org.slf4j.event.Level.WARN;

@Slf4j
@SuperBuilder
public class HtmlReportConsumer extends WebDriverEventConsumer {

    private final HtmlUtils htmlUtils = HtmlUtils.getInstance();

    private StatefulExtentTest statefulExtentTest;
    private TestData testData;
    private Video video;

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final ExtentTest currentNode = statefulExtentTest.getCurrentNode();
        final Frame frame = webDriverEvent.getFrame();
        final int frameNumber = video.getAndIncrementFrameNumberFor(testData, frame);
        final String message = webDriverEvent.getMessage();
        final Level level = webDriverEvent.getLevel();
        final String details = video.shouldRecord(frame) ? htmlUtils.buildFrameTagFor(frameNumber, message) : message;

        log.trace("Logging {}:'{}' at {} level", frameNumber, message, level);
        currentNode.log(WARN.equals(webDriverEvent.getLevel()) ? WARNING : INFO, details);
    }
}
