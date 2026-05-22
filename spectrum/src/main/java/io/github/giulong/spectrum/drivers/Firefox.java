package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.interfaces.BiDiDriver;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.GeckoDriverService.Builder;
import org.openqa.selenium.remote.service.DriverService;

public class Firefox extends Driver<FirefoxOptions, GeckoDriverService, GeckoDriverService.Builder> implements BiDiDriver<FirefoxOptions> {

    @Override
    public DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> getDriverServiceBuilder() {
        final Configuration.Drivers.Firefox.Service service = configuration.getDrivers().getFirefox().getService();

        return new GeckoDriverService.Builder()
                .withAllowHosts(service.getAllowHosts())
                .withLogLevel(service.getLogLevel())
                .withTruncatedLogs(service.isTruncatedLogs())
                .withProfileRoot(service.getProfileRoot());
    }

    @Override
    public Firefox buildCapabilities() {
        capabilities = new FirefoxOptions();

        return this;
    }

    @Override
    public Driver<FirefoxOptions, GeckoDriverService, Builder> mergeCapabilitiesWith(final Drivers drivers) {
        final Configuration.Drivers.Firefox firefox = drivers.getFirefox();
        final String binary = firefox.getBinary();

        capabilities = new FirefoxOptions().addArguments(firefox.getArgs());

        if (binary != null) {
            capabilities.setBinary(binary);
        }

        firefox.getCapabilities().forEach(capabilities::setCapability);
        firefox.getPreferences().forEach(capabilities::addPreference);
        activateBiDi(capabilities, drivers, firefox);

        return this;
    }
}
