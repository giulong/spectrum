package io.github.giulong.spectrum.utils.js;

import io.github.giulong.spectrum.interfaces.WebElementFinder;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsTest {

    @Mock
    private StringUtils stringUtils;

    @Mock
    private WebElement webElement;

    @Mock
    private List<WebElement> webElements;

    @Mock
    private WebElementFinder webElementFinder;

    @Mock
    private JavascriptExecutor webDriver;

    @InjectMocks
    private Js js;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("stringUtils", js, stringUtils);
    }

    @Test
    @DisplayName("click should click with javascript on the provided webElement and return the Js instance")
    void testClick() {
        assertEquals(js, js.click(webElement));

        verify(webDriver).executeScript("arguments[0].click();", webElement);
    }

    @Test
    @DisplayName("sendKeys should insert all the provided charSequences as value of the provided webElement and return the Js instance")
    void testSendKegitys() {
        final String string = "string";
        final String escapedString = "escapedString";
        final CharSequence[] keysToSend = new CharSequence[]{string, Keys.END, Keys.ADD};

        when(stringUtils.escape(string)).thenReturn(escapedString);

        assertEquals(js, js.sendKeys(webElement, keysToSend));

        verify(webDriver).executeScript(String.format("arguments[0].value='%s';", String.join("", escapedString, Keys.END, Keys.ADD)), webElement);
    }

    @Test
    @DisplayName("submit should submit with javascript the provided webElement and return the Js instance")
    void testSubmit() {
        assertEquals(js, js.submit(webElement));

        verify(webDriver).executeScript("arguments[0].submit();", webElement);
    }

    @Test
    @DisplayName("clear should delete the values with javascript from the provided webElement and return the Js instance")
    void testClear() {
        assertEquals(js, js.clear(webElement));

        verify(webDriver).executeScript("arguments[0].value='';", webElement);
    }

    @Test
    @DisplayName("findElement should execute using document when no context is passed")
    void testFindElementNoContext() {
        String locatorValue = "locatorValue";
        String escapedLocatorValue = "escapedLocatorValue";

        when(stringUtils.escape(locatorValue)).thenReturn(escapedLocatorValue);
        when(webElementFinder.findElement(webDriver, null, escapedLocatorValue)).thenReturn(webElement);

        WebElement result = js.findElement(webElementFinder, locatorValue);

        assertEquals(webElement, result);
        verify(webElementFinder).findElement(webDriver, null, escapedLocatorValue);
    }

    @Test
    @DisplayName("findElement should execute and return a WebElement using the given context")
    void testFindElementWithContext() {
        WebElement context = webElement;
        String locatorValue = "locatorValue";
        String escapedLocatorValue = "escapedLocatorValue";
        WebElement expectedWebElement = webElement;

        when(stringUtils.escape(locatorValue)).thenReturn(escapedLocatorValue);
        when(webElementFinder.findElement(webDriver, context, escapedLocatorValue)).thenReturn(expectedWebElement);

        WebElement result = js.findElement(context, webElementFinder, locatorValue);

        assertEquals(expectedWebElement, result);
        verify(webElementFinder).findElement(webDriver, context, escapedLocatorValue);
    }

    @Test
    @DisplayName("findElements should be executed without a context passed")
    void testFindElementsNoContext() {
        String locatorValue = "locatorValue";
        String escapedLocatorValue = "escapedLocatorValue";

        when(stringUtils.escape(locatorValue)).thenReturn(escapedLocatorValue);
        when(webElementFinder.findElements(webDriver, null, escapedLocatorValue)).thenReturn(webElements);

        List<WebElement> result = js.findElements(webElementFinder, locatorValue);

        assertEquals(webElements, result);
        verify(webElementFinder).findElements(webDriver, null, escapedLocatorValue);
    }

    @Test
    @DisplayName("findElements should execute and return a list of webElements between the given context")
    void testFindElementsWithContext() {
        WebElement context = webElement;
        String locatorValue = "testLocatorValue";
        String escapedLocatorValue = "escapedLocatorValue";
        List<WebElement> expectedWebElements = webElements;

        when(stringUtils.escape(locatorValue)).thenReturn(escapedLocatorValue);
        when(webElementFinder.findElements(webDriver, context, escapedLocatorValue)).thenReturn(expectedWebElements);

        List<WebElement> result = js.findElements(context, webElementFinder, locatorValue);

        assertEquals(expectedWebElements, result);
        verify(webElementFinder).findElements(webDriver, context, escapedLocatorValue);
    }

    @Test
    @DisplayName("getText should return the innerText of provided context")
    void testGetText() {
        when(webDriver.executeScript("return arguments[0].innerText;", webElement)).thenReturn("text");

        String result = js.getText(webElement);
        assertEquals("text", result);

        verify(webDriver).executeScript("return arguments[0].innerText;", webElement);
    }

    @Test
    @DisplayName("getCssValue should return the webElement with provided css properties")
    void testGetCssValue() {
        final String cssProperty = "cssProperty";
        when(webDriver.executeScript(String.format("return window.getComputedStyle(arguments[0]).getPropertyValue('%s');", cssProperty), webElement)).thenReturn("cssValue");

        String result = js.getCssValue(webElement, cssProperty);
        assertEquals("cssValue", result);
    }

    @Test
    @DisplayName("getShadowRoot should return the shadowRoot when it is present")
    void testGetShadowRootWhenPresent() {
        SearchContext expectedShadowRoot = mock(SearchContext.class);
        when(webDriver.executeScript("return arguments[0].shadowRoot;", webElement)).thenReturn(expectedShadowRoot);

        SearchContext actualShadowRoot = js.getShadowRoot(webElement);

        assertEquals(expectedShadowRoot, actualShadowRoot, "The returned shadowRoot should match the expected one.");
    }

    @Test
    @DisplayName("getShadowRoot should return null when the shadowRoot is not present")
    void testGetShadowRootWhenNotPresent() {
        when(webDriver.executeScript("return arguments[0].shadowRoot;", webElement)).thenReturn(null);

        assertNull(js.getShadowRoot(webElement));
    }

    @Test
    @DisplayName("getTagName should return the tag name of the provided WebElement in lowercase")
    void testGetTagName() {
        final String expectedTagName = "div";
        when(webDriver.executeScript("return arguments[0].tagName;", webElement)).thenReturn("DIV");

        String actualTagName = js.getTagName(webElement);

        assertEquals(expectedTagName, actualTagName, "The returned tag name should be in lowercase.");
        verify(webDriver).executeScript("return arguments[0].tagName;", webElement);
    }

    @Test
    @DisplayName("getDomAttribute should return the attribute value of the WebElement")
    void testGetDomAttribute() {
        final String attributeName = "attributeName";
        final String attributeValue = "attributeValue";
        when(webDriver.executeScript("return arguments[0].getAttribute('attributeName');", webElement))
                .thenReturn(attributeValue);

        String result = js.getDomAttribute(webElement, attributeName);

        assertEquals(attributeValue, result);
    }

    @Test
    @DisplayName("getDomProperty should return the property value of the WebElement")
    void testGetDomProperty() {
        final String propertyName = "propertyName";
        final String propertyValue = "propertyValue";
        when(webDriver.executeScript("return arguments[0].propertyName;", webElement)).thenReturn(propertyValue);

        String result = js.getDomProperty(webElement, propertyName);

        assertEquals(propertyValue, result);
    }

    @Test
    @DisplayName("getAttribute should return the DOM property if it is not null")
    void testGetAttributeReturnsProperty() {
        final String attributeName = "class";
        final String convertedAttributeName = "className";
        final String propertyValue = "button";

        when(js.getDomProperty(webElement, convertedAttributeName)).thenReturn(propertyValue);

        String result = js.getAttribute(webElement, attributeName);

        assertEquals(propertyValue, result);
        verify(webDriver, never()).executeScript("return arguments[0].type;");
    }

    @Test
    @DisplayName("getAttribute should return the DOM attribute if the property is null")
    void testGetAttributeReturnsAttributeWhenPropertyIsNull() {
        final String attributeName = "class";
        final String convertedAttributeName = "className";
        final String attributeValue = "button";

        when(js.getDomProperty(webElement, convertedAttributeName)).thenReturn(null);
        when(js.getDomAttribute(webElement, convertedAttributeName)).thenReturn(attributeValue);

        String result = js.getAttribute(webElement, attributeName);

        assertEquals(attributeValue, result);
        verify(webDriver, never()).executeScript("return arguments[0].getAttribute('type');");
    }

    @Test
    @DisplayName("isSelected should return true if the element is checked")
    void testIsSelected() {
        when(webDriver.executeScript("return arguments[0].checked;", webElement)).thenReturn(true);

        boolean result = js.isSelected(webElement);

        assertTrue(result);
    }

    @DisplayName("isEnabled should return true if the element is not disabled")
    @ParameterizedTest(name = "with js disabled {0}")
    @ValueSource(booleans = {true, false})
    void testIsEnabled(final boolean disabled) {
        when(webDriver.executeScript("return arguments[0].disabled;", webElement)).thenReturn(disabled);

        assertEquals(!disabled, js.isEnabled(webElement));
    }

    @Test
    @DisplayName("isDisplayed should return true if the element is displayed")
    void testIsDisplayed() {
        when(webDriver.executeScript("var rectangle = arguments[0].getBoundingClientRect();" +
                "return arguments[0].checkVisibility({visibilityProperty:true, opacityProperty:true}) && " +
                "rectangle.height > 0 && rectangle.width > 0", webElement)).thenReturn(true);

        boolean result = js.isDisplayed(webElement);

        assertTrue(result);
    }

    @Test
    @DisplayName("getSize should return the correct dimensions of the WebElement")
    void testGetSize() {
        List<Object> dimensions = Arrays.asList(200, 100);
        when(webDriver.executeScript(
                "var rectangle = arguments[0].getBoundingClientRect(); return [rectangle.width, rectangle.height];",
                webElement
        )).thenReturn(dimensions);

        Dimension result = js.getSize(webElement);

        assertNotNull(result);
        assertEquals(200, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    @Test
    @DisplayName("getRect should return the correct location and dimensions of the WebElement")
    void testGetRect() {
        List<Number> rectangleValues = Arrays.asList(50, 60, 200, 100);
        when(webDriver.executeScript(
                "var rectangle = arguments[0].getBoundingClientRect(); return [rectangle.x, rectangle.y, rectangle.width, rectangle.height];",
                webElement
        )).thenReturn(rectangleValues);

        Rectangle result = js.getRect(webElement);

        assertNotNull(result);
        assertEquals(50, result.x);
        assertEquals(60, result.y);
        assertEquals(200, result.width);
        assertEquals(100, result.height);
    }

    @Test
    @DisplayName("getLocation should return the correct top-left point of the WebElement")
    void testGetLocation() {
        List<Object> pointValues = Arrays.asList(50, 60);
        when(webDriver.executeScript(
                "var rectangle = arguments[0].getBoundingClientRect(); return [rectangle.x, rectangle.y];",
                webElement
        )).thenReturn(pointValues);

        Point result = js.getLocation(webElement);

        assertNotNull(result);
        assertEquals(50, result.x);
        assertEquals(60, result.y);
    }

    @Test
    @DisplayName("convertString should convert the input string into the value combined with the key in the map")
    void testConvert() {
        assertEquals("className", js.convert("class"));
        assertEquals("readOnly", js.convert("readonly"));
        assertEquals("notInMap", js.convert("notInMap"));
    }
}
