package io.github.giulong.spectrum;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
@Getter
public abstract class SpectrumPage<T extends SpectrumPage<T, Data>, Data> extends SpectrumEntity<T, Data> {

    private String endpoint;

    @SuppressWarnings("unchecked")
    public T open() {
        final String url = configuration.getApplication().getBaseUrl() + endpoint;
        log.info("Opening {}", url);
        driver.get(url);
        waitForPageLoading();

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T waitForPageLoading() {
        log.warn("Default no-op waitForPageLoading: override this method in your SpectrumPage!");

        return (T) this;
    }

    public boolean isLoaded() {
        final String currentUrl = driver.getCurrentUrl();
        final String pageUrl = configuration.getApplication().getBaseUrl() + endpoint;
        log.debug("Current url: {}", currentUrl);
        log.debug("Page url:    {}", pageUrl);

        return currentUrl.equals(pageUrl);
    }
}
