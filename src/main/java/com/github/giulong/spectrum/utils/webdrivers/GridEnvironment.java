package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
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
    public void buildFrom(final Browser<? extends MutableCapabilities> browser, final RemoteWebDriverBuilder webDriverBuilder) {
        log.info("Running on grid at {}", url);

        browser.mergeGridCapabilitiesFrom(capabilities);
        webDriverBuilder.address(url);
    }

    @Override
    public void finalizeSetupOf(final WebDriver webDriver) {
        if (localFileDetector) {
            ((RemoteWebDriver) webDriver).setFileDetector(new LocalFileDetector());
        }
    }
}
