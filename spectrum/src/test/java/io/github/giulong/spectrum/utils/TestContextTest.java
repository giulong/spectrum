package io.github.giulong.spectrum.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.function.Function;

import io.github.giulong.spectrum.MockFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class TestContextTest {

    @MockFinal
    @SuppressWarnings("unused")
    private Map<String, Object> store;

    @Mock
    private Function<String, String> function;

    @InjectMocks
    private TestContext testContext;

    @Test
    @DisplayName("put should put the provided key-value pair in the internal store")
    void put() {
        final String key = "key";
        final String value = "value";

        testContext.put(key, value);

        verify(store).put(key, value);
    }

    @Test
    @DisplayName("get should return the object associated to the the provided key casting it to the provided class")
    void get() {
        final String key = "key";
        final String value = "value";

        when(store.get(key)).thenReturn(value);

        assertEquals(value, testContext.get(key, String.class));
    }

    @Test
    @DisplayName("computeIfAbsent should call computeIfAbsent on the internal store, casting the returned object to the provided class")
    void computeIfAbsent() {
        final String key = "key";
        final String value = "value";

        when(store.computeIfAbsent(key, function)).thenReturn(value);

        assertEquals(value, testContext.computeIfAbsent(key, function, String.class));
    }
}
