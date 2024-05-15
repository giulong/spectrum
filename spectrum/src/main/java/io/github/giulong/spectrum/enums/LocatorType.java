package io.github.giulong.spectrum.enums;

import io.github.giulong.spectrum.interfaces.WebElementFinder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

@Getter
@AllArgsConstructor
public enum LocatorType implements WebElementFinder {

    ID("return %s.getElementById('%s');", "return %s.querySelectorAll('#%s');"),
    CLASS_NAME("return %s.getElementsByClassName('%s')[0];", "return %s.getElementsByClassName('%s');"),
    CSS_SELECTOR("return %s.querySelector('%s');", "return %s.querySelectorAll('%s');"),
    NAME("return %s.querySelector('[name=\"%s\"]');", "return %s.querySelectorAll('[name=\"%s\"]');"),
    TAG_NAME("return %s.getElementsByTagName('%s')[0];", "return %s.getElementsByTagName('%s');"),

    XPATH("return %s.evaluate('%s', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;",
            "var webElements = %s.evaluate('%s', document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);" +
                    "return Array.from({length: webElements.snapshotLength}, (_, i) => webElements.snapshotItem(i));"),

    LINK_TEXT("return %s.evaluate('//a[text()=\"%s\"]', " +
            "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;",
            "return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent === '%s');"),

    PARTIAL_LINK_TEXT("return %s.evaluate('//a[contains(text(), \"%s\")]', " +
            "document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;",
            "return Array.from(%s.querySelectorAll('a')).filter(link => link.textContent.includes('%s'));");

    private final String findElementScript;
    private final String findElementsScript;

    @Override
    public WebElement findElement(JavascriptExecutor driver, WebElement context, String locatorValue) {
        String jsCommand = String.format(findElementScript, context != null ? "arguments[0]" : "document", locatorValue);
        return (WebElement) driver.executeScript(jsCommand, context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WebElement> findElements(JavascriptExecutor driver, WebElement context, String locatorValue) {
        String jsCommand = String.format(findElementsScript, context != null ? "arguments[0]" : "document", locatorValue);
        return (List<WebElement>) driver.executeScript(jsCommand, context);
    }
}