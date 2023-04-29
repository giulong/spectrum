package com.giuliolongfils.spectrum.util;

import com.giuliolongfils.spectrum.internal.EventsListener;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.SystemProperties;
import com.giuliolongfils.spectrum.pojos.WebDriverWaits;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public abstract class SpectrumPage<Data> extends TakesScreenshots {

    @Getter
    protected WebDriver webDriver;
    protected Configuration configuration;
    protected Data data;
    protected SystemProperties systemProperties;
    protected EventsListener eventsListener;
    protected WebDriverWaits webDriverWaits;

    protected String endpoint;

    public void open() {
        final String url = configuration.getApplication().getBaseUrl() + endpoint;
        log.info("Opening {}", url);
        webDriver.get(url);
        waitForPageLoading();
    }

    public void waitForPageLoading() {
        log.warn("Default no-op waitForPageLoading: override this method in your SpectrumPage!");
    }

    public boolean isLoaded() {
        final String currentUrl = webDriver.getCurrentUrl();
        final String pageUrl = configuration.getApplication().getBaseUrl() + endpoint;
        log.debug("Current url: {}", currentUrl);
        log.debug("Page url:    {}", pageUrl);

        return currentUrl.equals(pageUrl);
    }
}
