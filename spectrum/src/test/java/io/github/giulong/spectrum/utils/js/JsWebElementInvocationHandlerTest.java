package io.github.giulong.spectrum.utils.js;

import io.github.giulong.spectrum.enums.LocatorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class JsWebElementInvocationHandlerTest {

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Matcher matcher;

    @Mock
    private WebElement webElement;

    @Mock
    private Js js;

    @Mock
    private Map<Method, Method> methods;

    @Mock
    private Method method;

    @Mock
    private Object result;

    @Mock
    private Method proxyMethod;

    @Mock
    private List<WebElement> webElements;

    @Mock
    private Point point;

    @Mock
    private Dimension dimension;

    @Mock
    private Rectangle rectangle;

    @Captor
    private ArgumentCaptor<Object[]> argsArgumentCaptor;

    @InjectMocks
    private JsWebElementInvocationHandler jsWebElementInvocationHandler;

    @Test
    @DisplayName("click should click with javascript on the provided webElement and return the Js instance")
    void click() {
        jsWebElementInvocationHandler.click();

        verify(js).click(webElement);
    }

    @Test
    @DisplayName("submit should delegate to js.submit")
    void submit() {
        jsWebElementInvocationHandler.submit();

        verify(js).submit(webElement);
    }

    @Test
    @DisplayName("sendKeys should delegate to js.sendKeys")
    void sendKeys() {
        jsWebElementInvocationHandler.sendKeys(Keys.END, "ok");

        verify(js).sendKeys(webElement, Keys.END, "ok");
    }

    @Test
    @DisplayName("clear should delegate to js.clear")
    void clear() {
        jsWebElementInvocationHandler.clear();

        verify(js).clear(webElement);
    }

    @Test
    @DisplayName("getTagName should delegate to js.getTagName")
    void getTagName() {
        final String tagName = "tagName";
        when(js.getTagName(webElement)).thenReturn(tagName);

        assertEquals(tagName, jsWebElementInvocationHandler.getTagName());
    }

    @Test
    @DisplayName("getDomProperty should delegate to js.getDomProperty")
    void getDomProperty() {
        final String name = "name";
        final String domProperty = "domProperty";
        when(js.getDomProperty(webElement, name)).thenReturn(domProperty);

        assertEquals(domProperty, jsWebElementInvocationHandler.getDomProperty(name));
    }

    @Test
    @DisplayName("getDomAttribute should delegate to js.getDomAttribute")
    void getDomAttribute() {
        final String name = "name";
        final String domAttribute = "domAttribute";
        when(js.getDomAttribute(webElement, name)).thenReturn(domAttribute);

        assertEquals(domAttribute, jsWebElementInvocationHandler.getDomAttribute(name));
    }

    @Test
    @DisplayName("getAttribute should delegate to js.getAttribute")
    void getAttribute() {
        final String name = "name";
        final String attribute = "attribute";
        when(js.getAttribute(webElement, name)).thenReturn(attribute);

        assertEquals(attribute, jsWebElementInvocationHandler.getAttribute(name));
    }

    @Test
    @DisplayName("getAriaRole should not be supported")
    void getAriaRole() {
        assertThrows(UnsupportedOperationException.class, () -> jsWebElementInvocationHandler.getAriaRole());
    }

    @Test
    @DisplayName("getAccessibleName should not be supported")
    void getAccessibleName() {
        assertThrows(UnsupportedOperationException.class, () -> jsWebElementInvocationHandler.getAccessibleName());
    }

    @Test
    @DisplayName("isSelected should delegate to js.isSelected")
    void isSelected() {
        when(js.isSelected(webElement)).thenReturn(true);

        assertTrue(jsWebElementInvocationHandler.isSelected());
    }

    @Test
    @DisplayName("isEnabled should delegate to js.isEnabled")
    void isEnabled() {
        when(js.isEnabled(webElement)).thenReturn(true);

        assertTrue(jsWebElementInvocationHandler.isEnabled());
    }

    @Test
    @DisplayName("getText should delegate to js.getText")
    void getText() {
        final String text = "text";
        when(js.getText(webElement)).thenReturn(text);

        assertEquals(text, jsWebElementInvocationHandler.getText());
    }

    @Test
    @DisplayName("findElements should delegate to js.findElements")
    void findElements() {
        final By by = By.id("id");

        when(js.findElements(webElement, LocatorType.from(by), "id")).thenReturn(webElements);

        assertEquals(webElements, jsWebElementInvocationHandler.findElements(by));
    }

    @Test
    @DisplayName("findElement should delegate to js.findElement")
    void findElement() {
        final By by = By.id("id");
        final WebElement anotherWebElement = mock(WebElement.class);

        when(js.findElement(webElement, LocatorType.from(by), "id")).thenReturn(anotherWebElement);

        assertEquals(anotherWebElement, jsWebElementInvocationHandler.findElement(by));
    }

    @Test
    @DisplayName("getShadowRoot should delegate to js.getShadowRoot")
    void getShadowRoot() {
        final WebElement anotherWebElement = mock(WebElement.class);

        when(js.getShadowRoot(webElement)).thenReturn(anotherWebElement);

        assertEquals(anotherWebElement, jsWebElementInvocationHandler.getShadowRoot());
    }

    @Test
    @DisplayName("isDisplayed should delegate to js.isDisplayed")
    void isDisplayed() {
        when(js.isDisplayed(webElement)).thenReturn(true);

        assertTrue(jsWebElementInvocationHandler.isDisplayed());
    }

    @Test
    @DisplayName("getLocation should delegate to js.getLocation")
    void getLocation() {
        when(js.getLocation(webElement)).thenReturn(point);

        assertEquals(point, jsWebElementInvocationHandler.getLocation());
    }

    @Test
    @DisplayName("getSize should delegate to js.getSize")
    void getSize() {
        when(js.getSize(webElement)).thenReturn(dimension);

        assertEquals(dimension, jsWebElementInvocationHandler.getSize());
    }

    @Test
    @DisplayName("getRect should delegate to js.getRect")
    void getRect() {
        when(js.getRect(webElement)).thenReturn(rectangle);

        assertEquals(rectangle, jsWebElementInvocationHandler.getRect());
    }

    @Test
    @DisplayName("getCssValue should delegate to js.getCssValue")
    void getCssValue() {
        final String propertyName = "propertyName";
        final String cssValue = "cssValue";
        when(js.getCssValue(webElement, propertyName)).thenReturn(cssValue);

        assertEquals(cssValue, jsWebElementInvocationHandler.getCssValue(propertyName));
    }

    @Test
    @DisplayName("getScreenshotAs should not be supported")
    void getScreenshotAs() {
        assertThrows(UnsupportedOperationException.class, () -> jsWebElementInvocationHandler.getScreenshotAs(BYTES));
    }

    @Test
    @DisplayName("invoke should find the wanted method with the same signature and call that instead of the original")
    void invoke() throws InvocationTargetException, IllegalAccessException {
        final String fullWebElement = "[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]";
        final String expected = "id: message";
        final String arg = "arg";
        final String methodName = "methodName";

        when(webElement.toString()).thenReturn(fullWebElement);
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(matcher.find()).thenReturn(true);
        when(matcher.group(1)).thenReturn(expected);

        when(method.getName()).thenReturn(methodName);
        when(methods.getOrDefault(method, method)).thenReturn(proxyMethod);
        when(proxyMethod.invoke(eq(jsWebElementInvocationHandler), argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, jsWebElementInvocationHandler.invoke(null, method, new Object[]{arg}));
        assertArrayEquals(new Object[]{arg}, argsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("invoke should find the wanted method with the same signature and call that instead of the original")
    void invokeNoLocatorMatch() throws InvocationTargetException, IllegalAccessException {
        final String fullWebElement = "[[not matching]";
        final String arg = "arg";
        final String methodName = "methodName";

        when(webElement.toString()).thenReturn(fullWebElement);
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(matcher.find()).thenReturn(false);

        when(method.getName()).thenReturn(methodName);
        when(methods.getOrDefault(method, method)).thenReturn(proxyMethod);
        when(proxyMethod.invoke(eq(jsWebElementInvocationHandler), argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, jsWebElementInvocationHandler.invoke(null, method, new Object[]{arg}));
        assertArrayEquals(new Object[]{arg}, argsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("extractLocatorValueFrom should return the locator value of the provided by")
    void extractLocatorValueFrom() {
        final By by = By.id("id");

        assertEquals("id", jsWebElementInvocationHandler.extractLocatorValueFrom(by));
    }
}
