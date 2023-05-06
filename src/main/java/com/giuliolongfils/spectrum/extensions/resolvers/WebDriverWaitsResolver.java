package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.WebDriverWaits;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class WebDriverWaitsResolver extends TypeBasedParameterResolver<WebDriverWaits> {

    public static final String WEB_DRIVER_WAITS = "webDriverWaits";

    @Override
    public WebDriverWaits resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        log.debug("Building WebDriverWaits");
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Configuration.WebDriver.Waits webDriverWaitsConfiguration = store.get(CONFIGURATION, Configuration.class).getWebDriver().getWaits();
        final WebDriver webDriver = store.get(WEB_DRIVER, WebDriver.class);
        final WebDriverWaits webDriverWaits = WebDriverWaits.builder()
                .pageLoadTimeout(new WebDriverWait(webDriver, webDriverWaitsConfiguration.getPageLoadTimeout()))
                .downloadTimeout(new WebDriverWait(webDriver, webDriverWaitsConfiguration.getDownloadTimeout()))
                .scriptTimeout(new WebDriverWait(webDriver, webDriverWaitsConfiguration.getScriptTimeout()))
                .instantTimeout(new WebDriverWait(webDriver, Duration.ZERO))
                .implicit(new WebDriverWait(webDriver, webDriverWaitsConfiguration.getImplicit()))
                .build();

        store.put(WEB_DRIVER_WAITS, webDriverWaits);
        return webDriverWaits;
    }
}
