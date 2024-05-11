package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.enums.LocatorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Js")
class JsTest {

    @Mock
    private WebElement webElement;

    @Mock
    private List<WebElement> webElements;

    @Mock
    private JavascriptExecutor webDriver;

    @Mock
    private JsMethodsUtils jsMethodsUtils;

    @Mock
    private JsStringUtils jsStringUtils;

    @InjectMocks
    private Js js;

    @Test
    @DisplayName("click should click with javascript on the provided webElement and return the Js instance")
    public void click() {
        assertEquals(js, js.click(webElement));

        verify(webDriver).executeScript("arguments[0].click();", webElement);
    }

    @Test
    @DisplayName("sendkeys should insert the input string as value of the provided webElement and return the Js instance")
    public void sendKeys() {
        final String keysToSend = "input Test";
        assertEquals(js, js.sendKeys(webElement, keysToSend));

        verify(webDriver).executeScript(String.format("arguments[0].value='%s';", keysToSend), webElement);
    }

    @Test
    @DisplayName("submit should submit with javascript the provided webElement and return the Js instance")
    public void submit() {
        assertEquals(js, js.submit(webElement));

        verify(webDriver).executeScript("arguments[0].submit();", webElement);
    }

    @Test
    @DisplayName("clear should delete the values with javascript from the provided webElement and return the Js instance")
    public void clear() {
        assertEquals(js, js.clear(webElement));

        verify(webDriver).executeScript("arguments[0].value='';", webElement);
    }

    @Test
    @DisplayName("findElement should be executed without a context passed")
    public void testFindElementNoContext() {
        final String locatorValue = "locatorValue";
        when(js.findElement(LocatorType.Id, locatorValue)).thenReturn(webElement);

        WebElement result = js.findElement(LocatorType.Id, locatorValue);
        assertSame(webElement, result);

        verify(webDriver).executeScript("return document.getElementById('locatorValue');");
    }

    @Test
    @DisplayName("findElement should execute and return a webElement between the given context")
    void testFindElementWithContext() {
        final String locatorValue = "locatorValue";
        when(js.findElement(webElement, LocatorType.className, locatorValue)).thenReturn(webElement);

        WebElement result = js.findElement(webElement, LocatorType.className, locatorValue);
        assertSame(webElement, result);

        verify(webDriver).executeScript("return arguments[0].getElementsByClassName('locatorValue')[0];", webElement);
    }

    @Test
    @DisplayName("findElements should be executed without a context passed")
    public void testFindElementsNoContext() {
        final String locatorValue = "locatorValue";
        when(js.findElements(LocatorType.Id, locatorValue)).thenReturn(webElements);

        List<WebElement> result = js.findElements(LocatorType.Id, locatorValue);
        assertSame(webElements, result);

        verify(webDriver).executeScript("return document.querySelectorAll('#locatorValue');");
    }

    @Test
    @DisplayName("findElements should execute and return a list of webElements between the given context")
    void testFindElementsWithContext() {
        final String locatorValue = "locatorValue";
        when(js.findElements(webElement, LocatorType.className, locatorValue)).thenReturn(webElements);

        List<WebElement> result = js.findElements(webElement, LocatorType.className, locatorValue);
        assertSame(webElements, result);

        verify(webDriver).executeScript("return arguments[0].getElementsByClassName('locatorValue');", webElement);
    }
}