package io.github.giulong.spectrum;

import io.github.giulong.spectrum.interfaces.Secured;
import io.github.giulong.spectrum.utils.Reflections;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

@Slf4j
@Getter
public abstract class SpectrumPage<T extends SpectrumPage<T, Data>, Data> extends SpectrumEntity<T, Data> {

    @SuppressWarnings("unused")
    private String endpoint;

    /**
     * Opens the web page at the URL made by the concatenation of the {@code baseUrl} provided in the {@code configuration.yaml}
     * and the value of the {@code @Endpoint} annotation on the calling SpectrumPage.
     * It also calls the {@link SpectrumPage#waitForPageLoading()}  waitForPageLoading} before returning
     *
     * @return the calling SpectrumPage instance
     */
    @SuppressWarnings("unchecked")
    public T open() {
        final String url = configuration.getApplication().getBaseUrl() + endpoint;
        log.info("Opening {}", url);
        driver.get(url);
        waitForPageLoading();

        return (T) this;
    }

    /**
     * This is a method that by default just logs a warning. If you need to check for custom conditions before considering
     * a page fully loaded, you should override this method, so that calling {@link SpectrumPage#open() open}
     * on pages will call your implementation automatically
     *
     * @return the calling SpectrumPage instance
     */
    @SuppressWarnings("unchecked")
    public T waitForPageLoading() {
        log.debug("Default no-op waitForPageLoading: override this method in your SpectrumPage!");

        return (T) this;
    }

    /**
     * Checks whether the SpectrumPage instance on which this is called is fully loaded
     *
     * @return true if the SpectrumPage is loaded
     */
    public boolean isLoaded() {
        final String currentUrl = driver.getCurrentUrl();
        final String pageUrl = configuration.getApplication().getBaseUrl() + endpoint;
        log.debug("Current url: {}", currentUrl);
        log.debug("Page url:    {}", pageUrl);

        return pageUrl.equals(currentUrl);
    }

    void addSecuredWebElements() {
        Reflections
                .getAnnotatedFieldsValues(this, Secured.class, WebElement.class)
                .forEach(testContext::addSecuredWebElement);
    }
}
