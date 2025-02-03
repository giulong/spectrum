package io.github.giulong.spectrum.internals.web_driver_listeners;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

@Slf4j
@SuperBuilder
public class AutoWaitWebDriverListener extends SpectrumWebDriverListener {

    private final Dimension noSize = new Dimension(0, 0);
    private final Point noLocation = new Point(0, 0);
    private Actions actions;
    private WebDriverWait webDriverWait;

    @Override
    public void beforeClick(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeSubmit(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeSendKeys(final WebElement element, final CharSequence... keysToSend) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeClear(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeGetTagName(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeGetAttribute(final WebElement element, final String name) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeIsSelected(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeIsEnabled(final WebElement element) {
        autoWaitFor(element, visibilityOf(element));
    }

    @Override
    public void beforeGetText(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeGetLocation(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeGetSize(final WebElement element) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    @Override
    public void beforeGetCssValue(final WebElement element, final String propertyName) {
        autoWaitFor(element, elementToBeClickable(element));
    }

    void autoWaitFor(final WebElement webElement, final ExpectedCondition<?>... conditions) {
        if (webElement.getLocation().equals(noLocation) && webElement.getSize().equals(noSize)) {
            log.trace("WebElement {} is hidden. Avoid auto-waiting", extractSelectorFrom(webElement));
            return;
        }

        log.trace("Auto-waiting before interacting with webElement {}", Arrays.toString(conditions));
        actions.scrollToElement(webElement).perform();
        webDriverWait.until(and(conditions));
    }
}
