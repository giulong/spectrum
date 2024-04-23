package io.github.giulong.spectrum.utils;

import lombok.Builder;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

@Builder
public class Js {

    private JavascriptExecutor driver;

    private final EscapeStringUtils escapeStringUtils = EscapeStringUtils.getInstance();

    /**
     * Get the Tag of the provided WebElement
     *
     * @param webElement the WebElement from which the tag name is taken
     * @return The tag name of the WebElement
     */
    public String getTagName(final WebElement webElement) {
        final String tagName = (String) driver.executeScript("return arguments[0].tagName", webElement);

        return tagName.toLowerCase();
    }

    /**
     * Get the size of the provided WebElement
     *
     * @param webElement the WebElement from which the size is taken
     * @return The rendered Size of the WebElement
     */
    public Dimension getSize(final WebElement webElement) {
        final List<Object> dimensions = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.width, rectangle.height];", webElement);

        return new Dimension(((Number) dimensions.get(0)).intValue(), ((Number) dimensions.get(1)).intValue());
    }

    /**
     * Get the location and size of the provided WebElement
     *
     * @param webElement the WebElement from which the location and size are taken
     * @return The location and size of the rendered WebElement
     */
    public Rectangle getRect(final WebElement webElement) {
        final List<Object> rectangle = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
                "return [rectangle.x, rectangle.y, rectangle.width, rectangle.height];", webElement);

        final Point point = new Point(((Number) rectangle.get(0)).intValue(), ((Number) rectangle.get(1)).intValue());
        final Dimension dimension = new Dimension(((Number) rectangle.get(2)).intValue(), ((Number) rectangle.get(3)).intValue());

        return new Rectangle(point, dimension);
    }

    /**
     * Get the location of the provided WebElement
     *
     * @param webElement the WebElement from which the location and size are taken
     * @return The top left-hand point of the rendered WebElement
     */
    public Point getLocation(final WebElement webElement) {
        final List<Object> point = (List<Object>) driver.executeScript("var rectangle = arguments[0].getBoundingClientRect(); " +
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
        final String jsCommand = String.format("arguments[0].value='%s';", escapeStringUtils.escapeString(keysToSend));
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
