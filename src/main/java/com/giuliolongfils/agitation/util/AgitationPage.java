package com.giuliolongfils.agitation.util;

import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.internal.EventListener;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.pojos.WebDriverWaits;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public abstract class AgitationPage extends TakesScreenshots {

    @Getter
    protected WebDriver webDriver;
    protected Configuration configuration;
    protected Data data;
    protected SystemProperties systemProperties;
    protected EventListener eventListener;
    protected WebDriverWaits webDriverWaits;

    protected String endpoint;

    public void open() {
        final String url = configuration.getApplication().getBaseUrl() + endpoint;
        log.info("Opening {}", url);
        webDriver.get(url);
        waitForPageLoading();
    }

    public void waitForPageLoading() {
        log.warn("Default no-op waitForPageLoading: override this method in your AgitationPage!");
    }

    public boolean isLoaded() {
        final String currentUrl = webDriver.getCurrentUrl();
        final String pageUrl = configuration.getApplication().getBaseUrl() + endpoint;
        log.debug("Current url: {}", currentUrl);
        log.debug("Page url:    {}", pageUrl);

        return currentUrl.equals(pageUrl);
    }
}
