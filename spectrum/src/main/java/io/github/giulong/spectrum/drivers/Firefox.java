package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverService;

public class Firefox extends Driver<FirefoxOptions, GeckoDriverService, GeckoDriverService.Builder> {

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
    void buildCapabilities() {
        final Configuration.Drivers.Firefox firefox = configuration.getDrivers().getFirefox();
        final String binary = firefox.getBinary();

        capabilities = new FirefoxOptions().addArguments(firefox.getArgs());

        if (binary != null) {
            capabilities.setBinary(binary);
        }

        firefox
                .getCapabilities()
                .forEach(capabilities::setCapability);

        firefox
                .getPreferences()
                .forEach(this::addPreference);
    }

    void addPreference(final String key, final Object value) {
        capabilities.addPreference(key, value instanceof Boolean || value instanceof Integer ? value : String.valueOf(value));
    }
}
