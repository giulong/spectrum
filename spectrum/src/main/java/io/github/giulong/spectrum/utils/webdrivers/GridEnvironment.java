package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class GridEnvironment extends Environment {

    protected URL url;
    protected final Map<String, String> capabilities = new HashMap<>();
    protected boolean localFileDetector;

    @Override
    public WebDriver setupFrom(final Browser<?, ?, ?> browser, final RemoteWebDriverBuilder webDriverBuilder) {
        log.info("Running on grid at {}", url);

        browser.mergeGridCapabilitiesFrom(capabilities);
        final RemoteWebDriver webDriver = (RemoteWebDriver) webDriverBuilder.address(url).build();

        if (localFileDetector) {
            webDriver.setFileDetector(new LocalFileDetector());
        }

        return webDriver;
    }

    @Override
    public void shutdown() {
        log.debug("Nothing to shutdown in a grid environment");
    }
}
