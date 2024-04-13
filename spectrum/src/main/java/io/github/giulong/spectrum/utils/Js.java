package io.github.giulong.spectrum.utils;

import lombok.Builder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

@Builder
public class Js {

    private JavascriptExecutor driver;

    /**
     * Performs a click with javascript on the provided WebElement
     *
     * @param webElement the WebElement to click on
     * @return the calling SpectrumEntity instance
     */
    public Js clickOn(final WebElement webElement) {
        driver.executeScript("arguments[0].click();", webElement);

        return this;
    }
}
