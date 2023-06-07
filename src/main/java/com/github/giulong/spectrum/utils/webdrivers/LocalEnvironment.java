package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;

@Slf4j
@Getter
@Setter
public class LocalEnvironment extends Environment {

    private boolean downloadWebDriver;

    @Override
    public WebDriver buildFrom(final Configuration configuration, final Browser<? extends MutableCapabilities> browser) {
        log.info("Running in local");

        final String driversPath = configuration.getRuntime().getDriversPath();
        if (downloadWebDriver) {
            browser
                    .getWebDriverManager()
                    .avoidOutputTree()
                    .cachePath(driversPath)
                    .setup();
        } else {
            log.warn("WebDriverManager disabled: using local webDriver");
            System.setProperty(browser.getSystemPropertyName(), Path.of(driversPath).resolve(browser.getDriverName()).toString());
        }

        return browser.buildWebDriver();
    }
}
