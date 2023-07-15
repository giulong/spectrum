package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

@Slf4j
public class LocalEnvironment extends Environment {

    @Override
    public void setupFrom(final Browser<?, ?, ?> browser, final RemoteWebDriverBuilder webDriverBuilder) {
        log.info("Running in local");
    }

    @Override
    public void finalizeSetupOf(final WebDriver webDriver) {
        log.debug("No additional setup needed for local webDriver");
    }
}
