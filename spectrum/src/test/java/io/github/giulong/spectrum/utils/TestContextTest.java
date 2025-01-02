package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static java.time.Duration.ZERO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestContextTest {

    @Mock
    private WebDriver driver;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @Mock
    private Duration implicitWaitTimeout;

    @Mock
    private Map<String, Object> store;

    @Mock
    private Function<String, String> function;

    @Mock
    private WebElement webElement1;

    @Mock
    private WebElement webElement2;

    @InjectMocks
    private TestContext testContext;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("store", testContext, store);
    }

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

    @Test
    @DisplayName("addSecuredWebElement should add the provided webElement to the internal list")
    void addSecuredWebElement() {
        testContext.addSecuredWebElement(webElement1);

        assertEquals(List.of(webElement1), Reflections.getFieldValue("securedWebElements", testContext));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("isSecuredWebElement should return true if the provided webElement is a secured one, skipping secured web elements that are not in the currently displayed page")
    void isSecuredWebElement() {
        Reflections.setField("securedWebElements", testContext, List.of(webElement1, webElement2));

        when(store.get(DRIVER)).thenReturn(driver);
        when(driver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(timeouts.getImplicitWaitTimeout()).thenReturn(implicitWaitTimeout);
        when(timeouts.implicitlyWait(ZERO)).thenReturn(timeouts);

        when(webElement1.getAccessibleName()).thenThrow(NoSuchElementException.class);
        when(webElement2.getAccessibleName()).thenReturn("");

        assertTrue(testContext.isSecuredWebElement(webElement2));

        verify(timeouts).implicitlyWait(implicitWaitTimeout);
    }

    @Test
    @DisplayName("isSecuredWebElement should return false if the provided webElement is not a secured one")
    void isSecuredWebElementFalse() {
        when(store.get(DRIVER)).thenReturn(driver);
        when(driver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(timeouts.getImplicitWaitTimeout()).thenReturn(implicitWaitTimeout);
        when(timeouts.implicitlyWait(ZERO)).thenReturn(timeouts);

        assertFalse(testContext.isSecuredWebElement(webElement1));
    }
}
