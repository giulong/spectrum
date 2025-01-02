package io.github.giulong.spectrum.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static java.time.Duration.ZERO;

@Slf4j
public class TestContext {

    private final Map<String, Object> store = new ConcurrentHashMap<>();
    private final List<WebElement> securedWebElements = new ArrayList<>();

    public void put(final String key, final Object value) {
        store.put(key, value);
    }

    public <T> T get(final String key, final Class<T> clazz) {
        return clazz.cast(store.get(key));
    }

    public <T> T computeIfAbsent(final String key, final Function<String, T> mappingFunction, final Class<T> clazz) {
        return clazz.cast(store.computeIfAbsent(key, mappingFunction));
    }

    public void addSecuredWebElement(final WebElement webElement) {
        securedWebElements.add(webElement);
    }

    public boolean isSecuredWebElement(final WebElement webElement) {
        final WebDriver driver = (WebDriver) store.get(DRIVER);
        final WebDriver.Timeouts timeouts = driver.manage().timeouts();
        final Duration implicitWaitTimeout = timeouts.getImplicitWaitTimeout();

        timeouts.implicitlyWait(ZERO);

        final boolean secured = securedWebElements
                .stream()
                .filter(securedWebElement -> {
                    try {
                        securedWebElement.getAccessibleName();
                        return true;
                    } catch (NoSuchElementException | UnsupportedOperationException ignored) {
                        log.error("Skipping SecureWebElement not in current page -> {}", securedWebElement);
                        return false;
                    }
                })
                .anyMatch(webElement::equals);

        timeouts.implicitlyWait(implicitWaitTimeout);
        return secured;
    }
}
