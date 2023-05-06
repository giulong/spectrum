package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.types.ImplicitWait;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ImplicitWaitResolver extends TypeBasedParameterResolver<ImplicitWait> {

    public static final String IMPLICIT_WAIT = "implicitWait";

    @Override
    public ImplicitWait resolveParameter(final ParameterContext parameterContext, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", IMPLICIT_WAIT);

        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final WebDriver webDriver = store.get(WEB_DRIVER, WebDriver.class);
        final Configuration.WebDriver.Waits waits = rootStore.get(CONFIGURATION, Configuration.class).getWebDriver().getWaits();
        final ImplicitWait webDriverWait = new ImplicitWait(webDriver, waits.getImplicit());

        store.put(IMPLICIT_WAIT, webDriverWait);
        return webDriverWait;
    }
}
