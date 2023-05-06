package com.giuliolongfils.spectrum.types;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ImplicitWait extends WebDriverWait {
    public ImplicitWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
