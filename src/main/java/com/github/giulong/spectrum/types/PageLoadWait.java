package com.github.giulong.spectrum.types;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PageLoadWait extends WebDriverWait {
    public PageLoadWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
