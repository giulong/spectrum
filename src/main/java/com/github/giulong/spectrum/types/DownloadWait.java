package com.github.giulong.spectrum.types;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DownloadWait extends WebDriverWait {
    public DownloadWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
