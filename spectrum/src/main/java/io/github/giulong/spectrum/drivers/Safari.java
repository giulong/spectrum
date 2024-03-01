package io.github.giulong.spectrum.drivers;

import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;

public class Safari extends Driver<SafariOptions, SafariDriverService, SafariDriverService.Builder> {

    @Override
    public DriverService.Builder<SafariDriverService, SafariDriverService.Builder> getDriverServiceBuilder() {
        return new SafariDriverService
                .Builder()
                .withLogging(configuration.getWebDriver().getSafari().isLogging());
    }

    @Override
    public void buildCapabilities() {
        capabilities = new SafariOptions();
    }
}
