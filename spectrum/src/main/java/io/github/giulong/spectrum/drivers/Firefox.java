package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverService;

public class Firefox extends Driver<FirefoxOptions, GeckoDriverService, GeckoDriverService.Builder> {

    @Override
    public DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> getDriverServiceBuilder() {
        return new GeckoDriverService.Builder();
    }

    @Override
    public void buildCapabilities() {
        final Configuration.WebDriver.Firefox firefoxConfig = configuration.getWebDriver().getFirefox();

        capabilities = new FirefoxOptions()
                .addArguments(firefoxConfig.getArgs())
                .setLogLevel(firefoxConfig.getLogLevel());

        firefoxConfig
                .getPreferences()
                .forEach(this::addPreference);
    }

    public void addPreference(final String key, final Object value) {
        capabilities.addPreference(key, value instanceof Boolean || value instanceof Integer ? value : String.valueOf(value));
    }
}
