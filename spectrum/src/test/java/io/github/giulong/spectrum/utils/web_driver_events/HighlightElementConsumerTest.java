package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HighlightElementConsumerTest {

    @Mock
    private WebElement webElement1;

    @Mock
    private WebElement webElement2;

    @Mock
    private JavascriptExecutor driver;

    @Mock
    private WebDriverEvent webDriverEvent;

    @InjectMocks
    private HighlightElementConsumer highlightElementConsumer = new HighlightElementConsumer(HighlightElementConsumer.builder());

    @Test
    @DisplayName("accept should execute the js script on all the web elements found in the provided webDriverEvent")
    void accept() {
        final String js = "js";

        Reflections.setField("js", highlightElementConsumer, js);

        when(webDriverEvent.findWebElementsInArgs()).thenReturn(List.of(webElement1, webElement2));

        highlightElementConsumer.accept(webDriverEvent);

        verify(driver).executeScript(js, webElement1);
        verify(driver).executeScript(js, webElement2);
    }
}
