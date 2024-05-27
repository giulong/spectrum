package io.github.giulong.spectrum.enums;

import io.github.giulong.spectrum.interfaces.WebElementFinder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.By.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum LocatorType implements WebElementFinder {

    ID(ById.class, "return %s.getElementById('%s');", "return %s.querySelectorAll('#%s');"),
    CLASS_NAME(ByClassName.class, "return %s.getElementsByClassName('%s')[0];", "return %s.getElementsByClassName('%s');"),
    CSS_SELECTOR(ByCssSelector.class, "return %s.querySelector('%s');", "return %s.querySelectorAll('%s');"),
    NAME(ByName.class, "return %s.querySelector('[name=\"%s\"]');", "return %s.querySelectorAll('[name=\"%s\"]');"),
    TAG_NAME(ByTagName.class, "return %s.getElementsByTagName('%s')[0];", "return %s.getElementsByTagName('%s');"),
    XPATH(ByXPath.class, "return %s.evaluate('%s', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;",
            "var webElements = %s.evaluate('%s', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);" +
                    "return Array.from({length: webElements.snapshotLength}, (_, i) => webElements.snapshotItem(i));"),

    LINK_TEXT(ByLinkText.class, "return %s.evaluate('//a[text()=\"%s\"]', " +
            "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;",
            "return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent === '%s');"),

    PARTIAL_LINK_TEXT(ByPartialLinkText.class, "return %s.evaluate('//a[contains(text(), \"%s\")]', " +
            "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;",
            "return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent.includes('%s'));");

    private final Class<? extends By> by;
    private final String findElementScript;
    private final String findElementsScript;

    @Override
    public WebElement findElement(JavascriptExecutor driver, WebElement context, String locatorValue) {
        String jsCommand = String.format(findElementScript, context != null ? "arguments[0]" : "document", locatorValue);
        final WebElement webElement = (WebElement) driver.executeScript(jsCommand, context);
        log.debug("Executing: {} => {}", jsCommand, webElement);
        return webElement;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WebElement> findElements(JavascriptExecutor driver, WebElement context, String locatorValue) {
        String jsCommand = String.format(findElementsScript, context != null ? "arguments[0]" : "document", locatorValue);
        final List<WebElement> webElements = (List<WebElement>) driver.executeScript(jsCommand, context);
        log.debug("Executing: {} => {}", jsCommand, webElements);
        return webElements;
    }

    public static LocatorType from(final By inputBy) {
        final Class<? extends By> byClass = inputBy.getClass();

        return Arrays
                .stream(values())
                .filter(v -> v.by.equals(byClass))
                .findFirst()
                .orElseThrow();
    }
}
