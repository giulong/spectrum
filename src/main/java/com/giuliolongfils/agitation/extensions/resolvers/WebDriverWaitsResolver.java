package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.WebDriverWaits;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WebDriverWaitsResolver extends TypeBasedParameterResolver<WebDriverWaits> {

    public static final String WEB_DRIVER_WAITS = "webDriverWaits";

    @Override
    public WebDriverWaits resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        final ContextManager contextManager = ContextManager.getInstance();
        final org.openqa.selenium.WebDriver webDriver = contextManager.getWebDriver(context);
        final Configuration.WebDriver webDriverPojo = contextManager.getConfiguration(context).getWebDriver();
        final WebDriverWaits webDriverWaits = WebDriverWaits.builder()
                .pageLoadingWait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverPojo.getPageLoadingWaitTimeout())))
                .downloadWait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverPojo.getDownloadWaitTimeout())))
                .scriptWait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverPojo.getScriptWaitTimeout())))
                .instantWait(new WebDriverWait(webDriver, Duration.ZERO))
                .wait(new WebDriverWait(webDriver, Duration.ofSeconds(webDriverPojo.getWaitTimeout())))
                .build();

        contextManager.store(WEB_DRIVER_WAITS, webDriverWaits, context);
        return webDriverWaits;
    }
}
