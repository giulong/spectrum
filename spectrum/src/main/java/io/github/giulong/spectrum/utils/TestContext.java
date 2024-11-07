package io.github.giulong.spectrum.utils;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
        return securedWebElements.contains(webElement);
    }
}
