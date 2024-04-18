package io.github.giulong.spectrum.utils;

import lombok.Builder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

@Builder
public class Js {

    private JavascriptExecutor driver;

    private final EscapeStringUtils escapeStringUtils = EscapeStringUtils.getInstance();

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
        driver.executeScript(jsCommand,webElement);

        return this;
    }

    /**
     * Clear input value with javascript on the provided WebElement
     *
     * @param webElement the WebElement to click on
     * @return the calling SpectrumEntity instance
     */
    public Js clear(final WebElement webElement) {
        driver.executeScript("arguments[0].value='';",webElement);

        return this;
    }
}
