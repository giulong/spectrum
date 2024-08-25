package io.github.giulong.spectrum.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TestContext {

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    public void put(final String key, final Object value) {
        store.put(key, value);
    }

    public <T> T get(final String key, final Class<T> clazz) {
        return clazz.cast(store.get(key));
    }

    public <T> T computeIfAbsent(final String key, final Function<String, T> mappingFunction, final Class<T> clazz) {
        return clazz.cast(store.computeIfAbsent(key, mappingFunction));
    }
}
