package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.browsers.Browser;
import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.internal.EventListener;
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

@Slf4j
public class WebDriverResolver extends TypeBasedParameterResolver<WebDriver> {

    public static final String WEB_DRIVER = "webDriver";

    @Override
    public WebDriver resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        final ContextManager contextManager = ContextManager.getInstance();
        final SystemProperties systemProperties = contextManager.getSystemProperties(context);
        final Configuration configuration = contextManager.getConfiguration(context);
        final Browser<?> browser = systemProperties.getBrowser();
        final WebDriverListener eventListener = EventListener.builder().build();
        final WebDriver webDriver = new EventFiringDecorator<>(eventListener).decorate(browser.build(configuration, systemProperties));
        contextManager.store(WEB_DRIVER, webDriver, context);

        return webDriver;
    }
}
