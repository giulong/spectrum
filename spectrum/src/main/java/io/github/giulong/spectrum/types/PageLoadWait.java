package io.github.giulong.spectrum.types;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageLoadWait extends WebDriverWait {
    public PageLoadWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
