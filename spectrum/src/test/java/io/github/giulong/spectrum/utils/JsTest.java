package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Js")
class JsTest {

    @Mock
    private WebElement webElement;

    @Mock
    private JavascriptExecutor webDriver;

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
}