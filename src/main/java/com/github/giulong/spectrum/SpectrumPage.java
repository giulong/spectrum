package com.github.giulong.spectrum;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SpectrumPage<Data> extends SpectrumEntity<Data> {

    protected String endpoint;

    public void open() {
        final String url = configuration.getApplication().getBaseUrl() + endpoint;
        log.info("Opening {}", url);
        webDriver.get(url);
        waitForPageLoading();
    }

    @Generated
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
