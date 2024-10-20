package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestContextTest {

    @Mock
    private Map<String, Object> store;

    @Mock
    private Function<String, String> function;

    @Mock
    private WebElement webElement;

    @InjectMocks
    private TestContext testContext;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("store", testContext, store);
    }

    @Test
    @DisplayName("put should put the provided key-value pair in the internal store")
    public void put() {
        final String key = "key";
        final String value = "value";

        testContext.put(key, value);

        verify(store).put(key, value);
    }

    @Test
    @DisplayName("get should return the object associated to the the provided key casting it to the provided class")
    public void get() {
        final String key = "key";
        final String value = "value";

        when(store.get(key)).thenReturn(value);

        assertEquals(value, testContext.get(key, String.class));
    }

    @Test
    @DisplayName("computeIfAbsent should call computeIfAbsent on the internal store, casting the returned object to the provided class")
    public void computeIfAbsent() {
        final String key = "key";
        final String value = "value";

        when(store.computeIfAbsent(key, function)).thenReturn(value);

        assertEquals(value, testContext.computeIfAbsent(key, function, String.class));
    }

    @Test
    @DisplayName("addSecuredWebElement should add the provided webElement to the internal list")
    public void addSecuredWebElement() {
        testContext.addSecuredWebElement(webElement);

        assertEquals(List.of(webElement), Reflections.getFieldValue("securedWebElements", testContext));
    }

    @Test
    @DisplayName("isSecuredWebElement should return true if the provided webElement is a secured one")
    public void isSecuredWebElement() {
        Reflections.setField("securedWebElements", testContext, List.of(webElement));

        assertTrue(testContext.isSecuredWebElement(webElement));
    }

    @Test
    @DisplayName("isSecuredWebElement should return false if the provided webElement is not a secured one")
    public void isSecuredWebElementFalse() {
        assertFalse(testContext.isSecuredWebElement(webElement));
    }
}