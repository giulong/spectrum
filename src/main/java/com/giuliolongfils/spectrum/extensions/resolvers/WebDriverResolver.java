package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.browsers.Browser;
import com.giuliolongfils.spectrum.internal.EventsListener;
import com.giuliolongfils.spectrum.pojos.Configuration;
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
    private final Configuration configuration;

    public WebDriverResolver(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public WebDriver resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        log.debug("Building WebDriver");
        final ExtensionContext.Store store = context.getRoot().getStore(GLOBAL);
        final Browser<?> browser = configuration.getSystemProperties().getBrowser();
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
