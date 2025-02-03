package io.github.giulong.spectrum;

import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.internals.page_factory.SpectrumFieldDecorator;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.js.JsWebElementListInvocationHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;

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
        final String pageUrl = String.format("%s/%s", configuration.getApplication().getBaseUrl(), endpoint.replaceFirst("/", ""));
        log.debug("Current url: {}", currentUrl);
        log.debug("Page url:    {}", pageUrl);

        return pageUrl.equals(currentUrl);
    }

    SpectrumPage<?, Data> init() {
        final String className = getClass().getSimpleName();
        log.debug("Injecting already resolved fields into an instance of {}", className);

        final Endpoint endpointAnnotation = getClass().getAnnotation(Endpoint.class);
        final String endpointValue = endpointAnnotation != null ? endpointAnnotation.value() : "";

        log.debug("The endpoint of page '{}' is '{}'", className, endpointValue);
        Reflections.setField("endpoint", this, endpointValue);

        final Duration autoWaitDuration = configuration.getDrivers().getWaits().getAuto().getTimeout();
        PageFactory.initElements(new SpectrumFieldDecorator(new AjaxElementLocatorFactory(driver, (int) autoWaitDuration.toSeconds())), this);

        Reflections
                .getAnnotatedFields(this, JsWebElement.class)
                .forEach(this::injectJsWebElementProxyInto);

        return this;
    }

    @SneakyThrows
    void injectJsWebElementProxyInto(final Field field) {
        final Object value = field.get(this);

        if (value instanceof List<?>) {
            log.debug("Field {} is a list. Cannot build proxy eagerly", field.getName());
            @SuppressWarnings("unchecked") final Object webElementProxy = Proxy.newProxyInstance(
                    List.class.getClassLoader(),
                    new Class<?>[]{List.class},
                    JsWebElementListInvocationHandler
                            .builder()
                            .jsWebElementProxyBuilder(jsWebElementProxyBuilder)
                            .webElements((List<WebElement>) value)
                            .build());

            field.set(this, webElementProxy);
            return;
        }

        field.set(this, jsWebElementProxyBuilder.buildFor(value));
    }
}
