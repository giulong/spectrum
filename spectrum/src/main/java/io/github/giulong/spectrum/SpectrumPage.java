package io.github.giulong.spectrum;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
@Getter
public abstract class SpectrumPage<T extends SpectrumPage<T, Data>, Data> extends SpectrumEntity<T, Data> {

    private String endpoint;

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
