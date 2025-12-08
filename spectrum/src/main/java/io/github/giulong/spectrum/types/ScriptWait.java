package io.github.giulong.spectrum.types;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ScriptWait extends WebDriverWait {
    public ScriptWait(final WebDriver driver, final Duration timeout) {
        super(driver, timeout);
    }
}
