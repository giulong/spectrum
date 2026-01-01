package io.github.giulong.spectrum.utils.events.html_report;

import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.nio.file.Path;

import com.aventstack.extentreports.ExtentTest;

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
        this.store = event.getContext().getStore(GLOBAL);
        this.testData = store.get(TEST_DATA, TestData.class);
        this.currentNode = store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class).getCurrentNode();
        this.screenshot = event.getPayload().getScreenshot();

        return true;
    }

    protected void addScreenshot(final Path path) {
        contextManager.getScreenshots().put(path.toString(), screenshot);
        fileUtils.write(path, screenshot);
    }
}
