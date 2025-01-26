package io.github.giulong.spectrum.utils.web_driver_events;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
@SuperBuilder
public class HighlightElementConsumer extends WebDriverEventConsumer {

    private JavascriptExecutor driver;
    private String js;

    @Override
    public void accept(final WebDriverEvent webDriverEvent) {
        final List<WebElement> webElements = webDriverEvent.findWebElementsInArgs();
        log.trace("Highlighting web elements: {}", webElements);

        webElements.forEach(webElement -> driver.executeScript(js, webElement));
    }
}
