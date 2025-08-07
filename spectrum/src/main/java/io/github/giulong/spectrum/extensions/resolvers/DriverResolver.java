package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.internals.web_driver_listeners.AutoWaitWebDriverListener;
import io.github.giulong.spectrum.internals.web_driver_listeners.EventsWebDriverListener;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.web_driver_events.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.decorators.Decorated;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DriverResolver extends TypeBasedParameterResolver<WebDriver> {

    public static final String DRIVER = "driver";
    public static final String ORIGINAL_DRIVER = "originalDriver";
    public static final String TEST_STEP_BUILDER_CONSUMER = "testStepBuilderConsumer";

    private final ContextManager contextManager = ContextManager.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();

    @Override
    public WebDriver resolveParameter(final ParameterContext arg0, final ExtensionContext context) {
        log.debug("Resolving {}", DRIVER);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final Configuration configuration = rootStore.get(CONFIGURATION, Configuration.class);
        final WebDriver driver = configuration.getRuntime().getDriver().build();
        final Configuration.Drivers drivers = configuration.getDrivers();
        final Configuration.Drivers.Events events = drivers.getEvents();
        final Configuration.Drivers.Waits.AutoWait autoWait = drivers.getWaits().getAuto();
        final StatefulExtentTest statefulExtentTest = store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class);
        final TestData testData = store.get(TEST_DATA, TestData.class);
        final Configuration.Application.Highlight highlight = configuration.getApplication().getHighlight();
        final Pattern locatorPattern = Pattern.compile(configuration.getExtent().getLocatorRegex());
        final Video video = configuration.getVideo();
        final LogConsumer logConsumer = LogConsumer
                .builder()
                .enabled(true)
                .build();

        final HtmlReportConsumer htmlReportConsumer = HtmlReportConsumer
                .builder()
                .enabled(true)
                .statefulExtentTest(statefulExtentTest)
                .testData(testData)
                .video(video)
                .build();

        final ScreenshotConsumer screenshotConsumer = ScreenshotConsumer
                .builder()
                .enabled(true)
                .video(video)
                .context(context)
                .build();

        final TestStepBuilderConsumer testStepBuilderConsumer = TestStepBuilderConsumer
                .builder()
                .enabled(true)
                .build();

        final HighlightElementConsumer highlightElementConsumer = HighlightElementConsumer
                .builder()
                .enabled(highlight.isEnabled())
                .driver((JavascriptExecutor) driver)
                .js(fileUtils.read(highlight.getJs()))
                .build();

        final List<WebDriverEventConsumer> consumers = List.of(logConsumer, htmlReportConsumer, screenshotConsumer, testStepBuilderConsumer, highlightElementConsumer);
        final List<WebDriverListener> webDriverListeners = new ArrayList<>();

        if (autoWait.isEnabled()) {
            webDriverListeners.add(AutoWaitWebDriverListener
                    .builder()
                    .actions(new Actions(driver))
                    .webDriverWait(new WebDriverWait(driver, autoWait.getTimeout()))
                    .locatorPattern(locatorPattern)
                    .build());
        }

        webDriverListeners.add(EventsWebDriverListener
                .builder()
                .locatorPattern(locatorPattern)
                .events(events)
                .consumers(consumers)
                .build());

        final WebDriver decoratedDriver = new EventFiringDecorator<>(webDriverListeners.toArray(new WebDriverListener[0])).decorate(driver);
        @SuppressWarnings("unchecked") final WebDriver originalDriver = ((Decorated<WebDriver>) decoratedDriver).getOriginal();

        store.put(TEST_STEP_BUILDER_CONSUMER, testStepBuilderConsumer);
        store.put(DRIVER, decoratedDriver);
        store.put(ORIGINAL_DRIVER, originalDriver);

        contextManager.put(context, TEST_STEP_BUILDER_CONSUMER, testStepBuilderConsumer);
        contextManager.put(context, DRIVER, decoratedDriver);
        contextManager.put(context, ORIGINAL_DRIVER, originalDriver);

        return decoratedDriver;
    }
}
