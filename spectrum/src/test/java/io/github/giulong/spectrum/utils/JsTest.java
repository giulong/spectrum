package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Js")
class JsTest {

    @Mock
    private WebElement webElement;

    @Mock(extraInterfaces = JavascriptExecutor.class)
    private WebDriver webDriver;

    @InjectMocks
    private Js js;

    @Test
    @DisplayName("clickOn should click with javascript on the provided webElement and return the Js instance")
    public void clickOn() {
        assertEquals(js, js.clickOn(webElement));

        verify((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", webElement);
    }
}