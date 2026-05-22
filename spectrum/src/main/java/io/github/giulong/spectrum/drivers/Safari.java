package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariDriverService.Builder;
import org.openqa.selenium.safari.SafariOptions;

public class Safari extends Driver<SafariOptions, SafariDriverService, SafariDriverService.Builder> {

    @Override
    public DriverService.Builder<SafariDriverService, SafariDriverService.Builder> getDriverServiceBuilder() {
        final Configuration.Drivers.Safari.Service service = configuration.getDrivers().getSafari().getService();

        return new SafariDriverService.Builder()
                .withLogging(service.isLogging());
    }

    @Override
    public Safari buildCapabilities() {
        capabilities = new SafariOptions();

        return this;
    }

    @Override
    public Driver<SafariOptions, SafariDriverService, Builder> mergeCapabilitiesWith(final Drivers drivers) {
        return this;
    }
}
