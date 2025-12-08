package io.github.giulong.spectrum.utils.js;

import static java.util.stream.Collectors.joining;

import java.util.*;

import io.github.giulong.spectrum.interfaces.WebElementFinder;
import io.github.giulong.spectrum.utils.StringUtils;

import lombok.Builder;

import org.openqa.selenium.*;

@Builder
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class Js {

    private static final Map<String, String> CONVERSION_MAP = new HashMap<>() {
        {
            put("class", "className");
            put("readonly", "readOnly");
        }
    };

    private final StringUtils stringUtils = StringUtils.getInstance();

    private JavascriptExecutor driver;

    /**
     * Find the first WebElement using the given method starting from the provided
     * context
     *
     * @param context the context where to search the element
     * @param webElementFinder locating mechanism for finding the WebElement
     * @param locatorValue the value used by the locating mechanism
     *
     * @return the found WebElement
     */
    public WebElement findElement(final WebElement context, final WebElementFinder webElementFinder, final String locatorValue) {
        return webElementFinder.findElement(driver, context, stringUtils.escape(locatorValue));
    }

    /**
     * Find the first WebElement using the given method starting from document
     *
     * @param webElementFinder the locating mechanism for finding the WebElement
     * @param locatorValue the value used by the locating mechanism
     *
     * @return the found WebElement
     */
    public WebElement findElement(final WebElementFinder webElementFinder, final String locatorValue) {
        return webElementFinder.findElement(driver, null, stringUtils.escape(locatorValue));
    }

    /**
     * Find all WebElements using the given method starting from the provided
     * context
     *
     * @param context the context where to search the elements
     * @param webElementFinder the locating mechanism for finding all WebElements
     * @param locatorValue the value used by the locating mechanism
     *
     * @return the list of found WebElements
     */
    public List<WebElement> findElements(final WebElement context, final WebElementFinder webElementFinder, final String locatorValue) {
        return webElementFinder.findElements(driver, context, stringUtils.escape(locatorValue));
    }

    /**
     * Find all WebElements using the given method starting from the provided
     * context
     *
     * @param webElementFinder the locating mechanism for finding all WebElements
     * @param locatorValue the value used by the locating mechanism
     *
     * @return the list of found WebElements
     */
    public List<WebElement> findElements(final WebElementFinder webElementFinder, final String locatorValue) {
        return webElementFinder.findElements(driver, null, stringUtils.escape(locatorValue));
    }

    /**
     * Get the innerText of the provided webElement
     *
     * @param webElement the WebElement from which the innerText has to be taken
     *
     * @return the value of the innerText
     */
    public String getText(final WebElement webElement) {
        return (String) driver.executeScript("return arguments[0].innerText;", webElement);
    }

    /**
     * Get the CSS Value of the provided property
     *
     * @param webElement the WebElement from which the tag CSS value is taken
     * @param cssProperty the CSS property to read
     *
     * @return the value of the CSS property as String
     */
    public String getCssValue(final WebElement webElement, final String cssProperty) {
        return (String) driver.executeScript(String.format("return window.getComputedStyle(arguments[0]).getPropertyValue('%s');", cssProperty), webElement);
    }

    /**
     * Get the shadowRoot of the provided WebElement
     *
     * @param webElement the WebElement from which the shadowRoot is taken
     *
     * @return the shadowRoot of the WebElement
     */
    public SearchContext getShadowRoot(final WebElement webElement) {
        return (SearchContext) driver.executeScript("return arguments[0].shadowRoot;", webElement);
    }

    /**
     * Get the Tag of the provided WebElement
     *
     * @param webElement the WebElement from which the tag name is taken
     *
     * @return the tag name of the WebElement
     */
    public String getTagName(final WebElement webElement) {
        final String tagName = (String) driver.executeScript("return arguments[0].tagName;", webElement);

        return tagName.toLowerCase();
    }

    /**
     * Get the static attribute of the provided WebElement
     *
     * @param webElement the WebElement from which the static attribute is taken
     * @param domAttribute the static Attribute to retrieve
     *
     * @return the DOM Attribute of the WebElement or null if there isn't
     */
    public String getDomAttribute(final WebElement webElement, final String domAttribute) {
        final String jsCommand = String.format("return arguments[0].getAttribute('%s');", domAttribute);

        return (String) driver.executeScript(jsCommand, webElement);
    }

    /**
     * Get the property of the provided WebElement
     *
     * @param webElement the webElement from which the property is taken
     * @param domProperty the property to retrieve
     *
     * @return the DOM property of the WebElement or null if there isn't
     */
    public String getDomProperty(final WebElement webElement, final String domProperty) {
        final String jsCommand = String.format("return arguments[0].%s;", domProperty);

        return (String) driver.executeScript(jsCommand, webElement);
    }

    /**
     * Get the property of the provided WebElement, if is null tries to take the dom
     * attribute with the same name
     *
     * @param webElement the webElement from which the property is taken
     * @param attribute the property/attribute to retrieve
     *
     * @return the attribute/property current value or null if the value is not set.
     */
    public String getAttribute(final WebElement webElement, final String attribute) {
        final String checkedAttribute = convert(attribute);
        final String domProperty = this.getDomProperty(webElement, checkedAttribute);

        return domProperty == null ? this.getDomAttribute(webElement, checkedAttribute) : domProperty;
    }

    /**
     * Determine whether the element is selected or not
     *
     * @param webElement the webElement to check
     *
     * @return true if element is currently selected/checked, false otherwise
     */
    public boolean isSelected(final WebElement webElement) {
        return (boolean) driver.executeScript("return arguments[0].checked;", webElement);
    }

    /**
     * Determine whether the element is enabled or not
     *
     * @param webElement the webElement to check
     *
     * @return true if element is enabled, false otherwise
     */
    public boolean isEnabled(final WebElement webElement) {
        final boolean disabled = (boolean) driver.executeScript("return arguments[0].disabled;", webElement);

        return !disabled;
    }

    /**
     * Determine whether the element is displayed or not
     *
     * @param webElement the webElement to check
     *
     * @return true if element is displayed, false otherwise
     */
    public boolean isDisplayed(final WebElement webElement) {
        return (boolean) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect();" +
                "return arguments[0].checkVisibility({visibilityProperty:true, opacityProperty:true}) && " +
                "rectangle.height > 0 && rectangle.width > 0",
                webElement);
    }

    /**
     * Get the size of the provided WebElement
     *
     * @param webElement the WebElement from which the size is taken
     *
     * @return the rendered Size of the WebElement
     */
    public Dimension getSize(final WebElement webElement) {
        @SuppressWarnings("unchecked")
        final List<Object> dimensions = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.width, rectangle.height];",
                webElement);

        return new Dimension(((Number) dimensions.get(0)).intValue(), ((Number) dimensions.get(1)).intValue());
    }

    /**
     * Get the location and size of the provided WebElement
     *
     * @param webElement the WebElement from which the location and size are taken
     *
     * @return the location and size of the rendered WebElement
     */
    public Rectangle getRect(final WebElement webElement) {
        @SuppressWarnings("unchecked")
        final List<Number> rectangle = (List<Number>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.x, rectangle.y, rectangle.width, rectangle.height];",
                webElement);

        final Point point = new Point((rectangle.get(0)).intValue(), (rectangle.get(1)).intValue());
        final Dimension dimension = new Dimension((rectangle.get(2)).intValue(), (rectangle.get(3)).intValue());

        return new Rectangle(point, dimension);
    }

    /**
     * Get the location of the provided WebElement
     *
     * @param webElement the WebElement from which the location and size are taken
     *
     * @return the top left-hand point of the rendered WebElement
     */
    public Point getLocation(final WebElement webElement) {
        @SuppressWarnings("unchecked")
        final List<Object> point = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.x, rectangle.y];",
                webElement);

        return new Point(((Number) point.get(0)).intValue(), ((Number) point.get(1)).intValue());
    }

    /**
     * Performs a click with javascript on the provided WebElement
     *
     * @param webElement the WebElement to click on
     *
     * @return the calling SpectrumEntity instance
     */
    public Js click(final WebElement webElement) {
        driver.executeScript("arguments[0].click();", webElement);

        return this;
    }

    /**
     * Send input value with javascript to the provided WebElement
     *
     * @param webElement the WebElement to target the send method
     * @param keysToSend the String to send to webElement
     *
     * @return the calling SpectrumEntity instance
     */
    public Js sendKeys(final WebElement webElement, final CharSequence... keysToSend) {
        final String escapedKeysToSend = Arrays
                .stream(keysToSend)
                .map(key -> key instanceof String ? stringUtils.escape((String) key) : key)
                .collect(joining());

        driver.executeScript(String.format("arguments[0].value='%s';", escapedKeysToSend), webElement);

        return this;
    }

    /**
     * Performs a submit action on the provided form
     *
     * @param webElement the WebElement to submit
     *
     * @return the calling SpectrumEntity instance
     */
    public Js submit(final WebElement webElement) {
        driver.executeScript("arguments[0].submit();", webElement);

        return this;
    }

    /**
     * Clear input value with javascript on the provided WebElement
     *
     * @param webElement the WebElement used to clear the input field
     *
     * @return the calling SpectrumEntity instance
     */
    public Js clear(final WebElement webElement) {
        driver.executeScript("arguments[0].value='';", webElement);

        return this;
    }

    protected String convert(final String stringToConvert) {
        return CONVERSION_MAP.getOrDefault(stringToConvert, stringToConvert);
    }
}
