package com.github.giulong.spectrum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SpectrumPage<T extends SpectrumPage<T, Data>, Data> extends SpectrumEntity<T, Data> {

    protected String endpoint;

    public T open() {
        final String url = configuration.getApplication().getBaseUrl() + endpoint;
        log.info("Opening {}", url);
        webDriver.get(url);
        waitForPageLoading();

        //noinspection unchecked
        return (T) this;
    }

    public T waitForPageLoading() {
        log.warn("Default no-op waitForPageLoading: override this method in your SpectrumPage!");

        //noinspection unchecked
        return (T) this;
    }

    public boolean isLoaded() {
        final String currentUrl = webDriver.getCurrentUrl();
        final String pageUrl = configuration.getApplication().getBaseUrl() + endpoint;
        log.debug("Current url: {}", currentUrl);
        log.debug("Page url:    {}", pageUrl);

        return currentUrl.equals(pageUrl);
    }
}
