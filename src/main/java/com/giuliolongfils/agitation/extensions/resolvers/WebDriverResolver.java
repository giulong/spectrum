package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.browsers.Browser;
import com.giuliolongfils.agitation.internal.EventsListener;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
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
    private final SystemProperties systemProperties;
    private final Configuration configuration;

    public WebDriverResolver(final SystemProperties systemProperties, final Configuration configuration) {
        this.systemProperties = systemProperties;
        this.configuration = configuration;
    }

    @Override
    public WebDriver resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        log.debug("Building WebDriver");
        final Browser<?> browser = systemProperties.getBrowser();
        final WebDriverListener eventListener = EventsListener.builder().build();
        final WebDriver webDriver = new EventFiringDecorator<>(eventListener).decorate(browser.build(configuration, systemProperties));

        context.getStore(GLOBAL).put(WEB_DRIVER, webDriver);
        return webDriver;
    }
}
