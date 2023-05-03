package com.giuliolongfils.spectrum.extensions.resolvers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ActionsResolver extends TypeBasedParameterResolver<Actions> {

    @Override
    public Actions resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        log.debug("Building Actions");
        return new Actions(context.getStore(GLOBAL).get(WEB_DRIVER, WebDriver.class));
    }
}
