package com.github.giulong.spectrum.utils.webdrivers;

import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
public class GridEnvironment extends Environment {

    @Override
    public WebDriver buildFrom(final Configuration configuration, final Browser<? extends MutableCapabilities> browser) {
        log.info("Running on grid");

        final Configuration.WebDriver.Grid gridConfiguration = configuration.getWebDriver().getGrid();
        browser.mergeGridCapabilitiesFrom(gridConfiguration);

        return RemoteWebDriver
                .builder()
                .oneOf(browser.getCapabilities())
                .address(gridConfiguration.getUrl())
                .build();
    }
}
