package io.github.giulong.spectrum.internals.web_driver_listeners;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Arrays;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
@SuperBuilder
public class AutoWaitWebDriverListener extends SpectrumWebDriverListener {

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
        final String selector = extractSelectorFrom(webElement);

        try {
            log.trace("Scrolling to webElement located by {}", selector);
            actions.moveToElement(webElement).perform();
        } catch (ElementNotInteractableException | MoveTargetOutOfBoundsException ignored) {
            log.trace("WebElement located by {} not interactable. Scrolling avoided.", selector);
        }

        log.trace("Auto-waiting for conditions: {}", Arrays.toString(conditions));
        webDriverWait.until(and(conditions));
    }
}
