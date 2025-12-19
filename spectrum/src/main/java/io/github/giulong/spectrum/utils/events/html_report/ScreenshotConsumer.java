package io.github.giulong.spectrum.utils.events.html_report;

import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.nio.file.Path;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsConsumer;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public abstract class ScreenshotConsumer extends EventsConsumer {

    protected final FileUtils fileUtils = FileUtils.getInstance();
    protected final HtmlUtils htmlUtils = HtmlUtils.getInstance();
    protected final Configuration configuration = Configuration.getInstance();
    protected final ContextManager contextManager = ContextManager.getInstance();

    protected ExtensionContext.Store store;
    protected TestData testData;
    protected ExtentTest currentNode;
    protected byte[] screenshot;

    @Override
    protected boolean shouldAccept(final Event event) {
        final Map<String, Object> payload = event.getPayload();

        this.store = event.getContext().getStore(GLOBAL);
        this.testData = store.get(TEST_DATA, TestData.class);
        this.currentNode = store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class).getCurrentNode();
        this.screenshot = (byte[]) payload.get(SCREENSHOT);

        return true;
    }

    protected void addScreenshot(final Path path) {
        contextManager.getScreenshots().put(path.toString(), screenshot);
        fileUtils.write(path, screenshot);
    }

    protected void addScreenshotToReport(final Path path, final Status status, final String tag, final Media media) {
        currentNode.log(status, tag, media);
        addScreenshot(path);
    }
}
