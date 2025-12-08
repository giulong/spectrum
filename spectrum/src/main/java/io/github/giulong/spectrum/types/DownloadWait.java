package io.github.giulong.spectrum.types;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DownloadWait extends WebDriverWait {
    public DownloadWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
