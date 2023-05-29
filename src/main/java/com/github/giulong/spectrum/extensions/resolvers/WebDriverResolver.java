package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.internals.EventsListener;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class WebDriverResolver extends TypeBasedParameterResolver<WebDriver> {

    public static final String WEB_DRIVER = "webDriver";

    @Override
    public WebDriver resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", WEB_DRIVER);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Configuration configuration = store.get(ConfigurationResolver.CONFIGURATION, Configuration.class);
        final Browser<?> browser = configuration.getRuntime().getBrowser();
        final WebDriver webDriver = browser.build(configuration);

        if (!configuration.getWebDriver().isDefaultEventListenerEnabled()) {
            store.put(WEB_DRIVER, webDriver);
            return webDriver;
        }

        final WebDriverListener eventListener = EventsListener.builder()
                .store(store)
                .events(configuration.getEvents())
                .build();
        final WebDriver decoratedWebDriver = new EventFiringDecorator<>(eventListener).decorate(webDriver);

        store.put(WEB_DRIVER, decoratedWebDriver);
        return decoratedWebDriver;
    }
}
