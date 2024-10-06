package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.internals.SpectrumWebDriverListener;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.web_driver_events.HtmlReportConsumer;
import io.github.giulong.spectrum.utils.web_driver_events.LogConsumer;
import io.github.giulong.spectrum.utils.web_driver_events.ScreenshotConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.List;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DriverResolver extends TypeBasedParameterResolver<WebDriver> {

    public static final String DRIVER = "driver";

    private final ContextManager contextManager = ContextManager.getInstance();

    @Override
    public WebDriver resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", DRIVER);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final Configuration configuration = rootStore.get(CONFIGURATION, Configuration.class);
        final WebDriver driver = configuration.getRuntime().getDriver().build();
        final Configuration.Drivers.Events events = configuration.getDrivers().getEvents();
        final LogConsumer logConsumer = LogConsumer.builder().build();

        final HtmlReportConsumer htmlReportConsumer = HtmlReportConsumer
                .builder()
                .statefulExtentTest(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class))
                .build();

        final ScreenshotConsumer screenshotConsumer = ScreenshotConsumer
                .builder()
                .driver((TakesScreenshot) driver)
                .testData(store.get(TEST_DATA, TestData.class))
                .video(configuration.getVideo())
                .build();

        final WebDriverListener webDriverListener = SpectrumWebDriverListener.builder()
                .locatorPattern(Pattern.compile(configuration.getExtent().getLocatorRegex()))
                .events(events)
                .consumers(List.of(logConsumer, htmlReportConsumer, screenshotConsumer))
                .build();

        final WebDriver decoratedDriver = new EventFiringDecorator<>(webDriverListener).decorate(driver);

        store.put(DRIVER, decoratedDriver);
        contextManager.put(context, DRIVER, decoratedDriver);

        return decoratedDriver;
    }
}
