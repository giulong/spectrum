package io.github.giulong.spectrum.types;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ImplicitWait extends WebDriverWait {
    public ImplicitWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
