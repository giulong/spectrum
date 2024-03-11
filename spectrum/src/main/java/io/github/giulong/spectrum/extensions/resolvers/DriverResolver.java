package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.internals.EventsListener;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DriverResolver extends TypeBasedParameterResolver<WebDriver> {

    public static final String DRIVER = "driver";

    @Override
    public WebDriver resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", DRIVER);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final Configuration configuration = rootStore.get(CONFIGURATION, Configuration.class);
        final WebDriver driver = configuration.getRuntime().getDriver().build();
        final WebDriverListener eventListener = EventsListener.builder()
                .locatorPattern(Pattern.compile(configuration.getExtent().getLocatorRegex()))
                .extentTest(store.get(EXTENT_TEST, ExtentTest.class))
                .video(configuration.getVideo())
                .testData(store.get(TEST_DATA, TestData.class))
                .driver(driver)
                .events(configuration.getDrivers().getEvents())
                .build();
        final WebDriver decoratedDriver = new EventFiringDecorator<>(eventListener).decorate(driver);

        store.put(DRIVER, decoratedDriver);
        return decoratedDriver;
    }
}
