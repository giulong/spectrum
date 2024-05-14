package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.enums.LocatorType;
import lombok.Builder;
import org.openqa.selenium.*;

import java.util.List;

@Builder
public class Js {

    private JavascriptExecutor driver;

    private final StringUtils stringUtils = StringUtils.getInstance();

    /**
     * Find the first WebElement using the given method starting from the provided context
     *
     * @param context      the context where to search the element
     * @param locatorType  the locating mechanism for finding the WebElement
     * @param locatorValue the value used by the locating mechanism
     * @return the found WebElement
     */
    public WebElement findElement(final WebElement context, final LocatorType locatorType, final String locatorValue) {
        return locatorType.findElement(driver, context, stringUtils.escape(locatorValue));
    }

    /**
     * Find the first WebElement using the given method starting from document
     *
     * @param locatorType  the locating mechanism for finding the WebElement
     * @param locatorValue the value used by the locating mechanism
     * @return the found WebElement
     */
    public WebElement findElement(final LocatorType locatorType, final String locatorValue) {
        return locatorType.findElement(driver, null, stringUtils.escape(locatorValue));
    }

    /**
     * Find all WebElements using the given method starting from the provided context
     *
     * @param context      the context where to search the elements
     * @param locatorType  the locating mechanism for finding all WebElements
     * @param locatorValue the value used by the locating mechanism
     * @return the list of found WebElements
     */
    public List<WebElement> findElements(final WebElement context, final LocatorType locatorType, final String locatorValue) {
        return locatorType.findElements(driver, context, stringUtils.escape(locatorValue));
    }

    /**
     * Find all WebElements using the given method starting from the provided context
     *
     * @param locatorType  the locating mechanism for finding all WebElements
     * @param locatorValue the value used by the locating mechanism
     * @return the list of found WebElements
     */
    public List<WebElement> findElements(final LocatorType locatorType, final String locatorValue) {
        return locatorType.findElements(driver, null, stringUtils.escape(locatorValue));
    }

    /**
     * Get the innerText of the provided webElement
     *
     * @param webElement the WebElement from which the innerText has to be taken
     * @return the value of the innerText
     */
    public String getText(final WebElement webElement) {
        return (String) driver.executeScript("return arguments[0].innerText;", webElement);
    }

    /**
     * Get the CSS Value of the provided property
     *
     * @param webElement  the WebElement from which the tag CSS value is taken
     * @param cssProperty the CSS property to read
     * @return the value of the CSS property as String
     */
    public String getCssValue(final WebElement webElement, final String cssProperty) {
        return (String) driver.executeScript(String.format("return window.getComputedStyle(arguments[0]).getPropertyValue('%s');", cssProperty), webElement);
    }

    /**
     * Get the shadowRoot of the provided WebElement
     *
     * @param webElement the WebElement from which the shadowRoot is taken
     * @return the shadowRoot of the WebElement
     */
    public SearchContext getShadowRoot(final WebElement webElement) {
        SearchContext shadowRoot = (SearchContext) driver.executeScript("return arguments[0].shadowRoot;", webElement);
        if (shadowRoot == null) {
            throw new NoSuchShadowRootException("No shadow root could be found for the provided webElement");
        }
        return shadowRoot;
    }

    /**
     * Get the Tag of the provided WebElement
     *
     * @param webElement the WebElement from which the tag name is taken
     * @return the tag name of the WebElement
     */
    public String getTagName(final WebElement webElement) {
        final String tagName = (String) driver.executeScript("return arguments[0].tagName;", webElement);

        return tagName.toLowerCase();
    }

    /**
     * Get the static attribute of the provided WebElement
     *
     * @param webElement   the WebElement from which the static attribute is taken
     * @param domAttribute the static Attribute to retrieve
     * @return the DOM Attribute of the WebElement or null if there isn't
     */
    public String getDomAttribute(final WebElement webElement, final String domAttribute) {
        final String jsCommand = String.format("return arguments[0].getAttribute('%s');", domAttribute);

        return (String) driver.executeScript(jsCommand, webElement);
    }

    /**
     * Get the property of the provided WebElement
     *
     * @param webElement  the webElement from which the property is taken
     * @param domProperty the property to retrieve
     * @return the DOM property of the WebElement or null if there isn't
     */
    public String getDomProperty(final WebElement webElement, final String domProperty) {
        final String jsCommand = String.format("return arguments[0].%s;", domProperty);

        return (String) driver.executeScript(jsCommand, webElement);
    }

    /**
     * Get the property of the provided WebElement, if is null tries to take the dom attribute with the same name
     *
     * @param webElement the webElement from which the property is taken
     * @param attribute  the property/attribute to retrieve
     * @return the attribute/property current value or null if the value is not set.
     */
    public String getAttribute(final WebElement webElement, final String attribute) {
        final String domProperty = this.getDomProperty(webElement, stringUtils.convertCssProperty(attribute));
        if (domProperty == null) {
            return this.getDomAttribute(webElement, stringUtils.convertCssProperty(attribute));
        }
        return domProperty;
    }

    /**
     * Determine whether the element is selected or not
     *
     * @param webElement the webElement to check
     * @return true if element is currently selected/checked, false otherwise
     */
    public boolean isSelected(final WebElement webElement) {
        return (boolean) driver.executeScript("return arguments[0].checked;", webElement);
    }

    /**
     * Determine whether the element is enabled or not
     *
     * @param webElement the webElement to check
     * @return true if element is enabled, false otherwise
     */
    public boolean isEnabled(final WebElement webElement) {
        final boolean isDisabled = (boolean) driver.executeScript("return arguments[0].disabled;", webElement);

        return !isDisabled;
    }

    /**
     * Determine whether the element is displayed or not
     *
     * @param webElement the webElement to check
     * @return true if element is displayed, false otherwise
     */
    public boolean isDisplayed(final WebElement webElement) {
        return (boolean) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect();" +
                "return arguments[0].checkVisibility({visibilityProperty:true, opacityProperty:true}) && " +
                "rectangle.height > 0 && rectangle.width > 0", webElement);
    }

    /**
     * Get the size of the provided WebElement
     *
     * @param webElement the WebElement from which the size is taken
     * @return the rendered Size of the WebElement
     */
    public Dimension getSize(final WebElement webElement) {
        @SuppressWarnings("unchecked") final List<Object> dimensions = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.width, rectangle.height];", webElement);

        return new Dimension(((Number) dimensions.get(0)).intValue(), ((Number) dimensions.get(1)).intValue());
    }

    /**
     * Get the location and size of the provided WebElement
     *
     * @param webElement the WebElement from which the location and size are taken
     * @return the location and size of the rendered WebElement
     */
    public Rectangle getRect(final WebElement webElement) {
        @SuppressWarnings("unchecked") final List<Object> rectangle = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.x, rectangle.y, rectangle.width, rectangle.height];", webElement);

        final Point point = new Point(((Number) rectangle.get(0)).intValue(), ((Number) rectangle.get(1)).intValue());
        final Dimension dimension = new Dimension(((Number) rectangle.get(2)).intValue(), ((Number) rectangle.get(3)).intValue());

        return new Rectangle(point, dimension);
    }

    /**
     * Get the location of the provided WebElement
     *
     * @param webElement the WebElement from which the location and size are taken
     * @return the top left-hand point of the rendered WebElement
     */
    public Point getLocation(final WebElement webElement) {
        @SuppressWarnings("unchecked") final List<Object> point = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.x, rectangle.y];", webElement);

        return new Point(((Number) point.get(0)).intValue(), ((Number) point.get(1)).intValue());
    }

    /**
     * Performs a click with javascript on the provided WebElement
     *
     * @param webElement the WebElement to click on
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
     * @return the calling SpectrumEntity instance
     */
    public Js sendKeys(final WebElement webElement, final String keysToSend) {
        final String jsCommand = String.format("arguments[0].value='%s';", stringUtils.escape(keysToSend));
        driver.executeScript(jsCommand, webElement);

        return this;
    }

    /**
     * Performs a submit action on the provided form
     *
     * @param webElement the WebElement to submit
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
     * @return the calling SpectrumEntity instance
     */
    public Js clear(final WebElement webElement) {
        driver.executeScript("arguments[0].value='';", webElement);

        return this;
    }
}