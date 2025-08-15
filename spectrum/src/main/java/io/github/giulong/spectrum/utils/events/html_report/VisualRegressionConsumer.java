package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Path;
import java.util.Map;

import static com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromPath;
import static io.github.giulong.spectrum.enums.Frame.MANUAL;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestContextResolver.EXTENSION_CONTEXT;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.ScreenshotConsumer.SCREENSHOT;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public abstract class VisualRegressionConsumer extends EventsConsumer {

    protected final FileUtils fileUtils = FileUtils.getInstance();
    protected final HtmlUtils htmlUtils = HtmlUtils.getInstance();

    private final Configuration configuration = Configuration.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    protected Path referencePath;
    protected Path regressionPath;
    protected TestData testData;
    protected ExtentTest currentNode;
    protected byte[] screenshot;
    protected int frameNumber;

    @Override
    protected boolean shouldAccept(final Event event) {
        if (configuration.getVisualRegression().isEnabled()) {
            final Map<String, Object> payload = event.getPayload();
            final ExtensionContext.Store store = ((ExtensionContext) payload.get(EXTENSION_CONTEXT)).getStore(GLOBAL);

            this.testData = store.get(TEST_DATA, TestData.class);
            this.regressionPath = testData.getVisualRegression().getPath();
            this.referencePath = regressionPath.resolve(fileUtils.getScreenshotNameFrom(testData));
            this.currentNode = store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class).getCurrentNode();
            this.screenshot = (byte[]) payload.get(SCREENSHOT);
            this.frameNumber = configuration.getVideo().getAndIncrementFrameNumberFor(testData, MANUAL);

            return true;
        }

        return false;
    }

    void generateAndAddScreenshotFrom(final Event event) {
        final Map<String, Object> payload = event.getPayload();
        final String message = (String) payload.get("message");
        final Status status = (Status) payload.get("status");
        final String tag = htmlUtils.buildFrameTagFor(frameNumber, message, testData, "screenshot-message");

        addScreenshot(referencePath, status, tag, createScreenCaptureFromPath(referencePath.toString()).build());
        testData.incrementScreenshotNumber();
    }

    void addScreenshot(final Path path, final Status status, final String tag, final Media media) {
        currentNode.log(status, tag, media);
        contextManager.getScreenshots().put(path.toString(), screenshot);
        fileUtils.write(path, screenshot);
    }
}
