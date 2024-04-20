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
}