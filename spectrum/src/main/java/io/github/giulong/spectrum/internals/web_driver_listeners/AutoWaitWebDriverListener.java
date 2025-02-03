package io.github.giulong.spectrum.internals.web_driver_listeners;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

@Slf4j
@Builder
public class AutoWaitWebDriverListener implements WebDriverListener {

    private final Dimension noSize = new Dimension(0, 0);
    private final Point noLocation = new Point(0, 0);
    private Actions actions;
    private WebDriverWait webDriverWait;
    private Pattern locatorPattern;

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

    String extractSelectorFrom(final WebElement webElement) {
        final String fullWebElement = webElement.toString();
        final Matcher matcher = locatorPattern.matcher(fullWebElement);

        final List<String> locators = new ArrayList<>();
        while (matcher.find()) {
            locators.add(matcher.group(1));
        }

        return String.join(" -> ", locators);
    }
}
