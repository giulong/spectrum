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

import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class WebDriverWaitsResolver extends TypeBasedParameterResolver<WebDriverWaits> {

    public static final String WEB_DRIVER_WAITS = "webDriverWaits";
    private final Configuration.WebDriver webDriverConfiguration;

    public WebDriverWaitsResolver(final Configuration.WebDriver webDriverConfiguration) {
        this.webDriverConfiguration = webDriverConfiguration;
    }

    @Override
    public WebDriverWaits resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        log.debug("Building WebDriverWaits");
        final ExtensionContext.Store store = context.getRoot().getStore(GLOBAL);
        final WebDriver webDriver = store.get(WEB_DRIVER, WebDriver.class);
        final WebDriverWaits webDriverWaits = WebDriverWaits.builder()
                .pageLoadingWait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverConfiguration.getPageLoadingWaitTimeout())))
                .downloadWait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverConfiguration.getDownloadWaitTimeout())))
                .scriptWait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverConfiguration.getScriptWaitTimeout())))
                .instantWait(new WebDriverWait(webDriver, Duration.ZERO))
                .wait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverConfiguration.getWaitTimeout())))
                .build();

        store.put(WEB_DRIVER_WAITS, webDriverWaits);
        return webDriverWaits;
    }
}
