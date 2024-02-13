package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import java.util.Map;

public class Firefox extends Driver<FirefoxOptions, GeckoDriverService, GeckoDriverService.Builder> {

    @Override
    public DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> getDriverServiceBuilder() {
        return new GeckoDriverService.Builder();
    }

    @Override
    public void buildCapabilities() {
        final Configuration.WebDriver.Firefox firefoxConfig = configuration.getWebDriver().getFirefox();

        capabilities = new FirefoxOptions();
        capabilities.addArguments(firefoxConfig.getArgs());
        capabilities.setLogLevel(firefoxConfig.getLogLevel());

        firefoxConfig.getPreferences().forEach(this::addPreference);
    }

    @Override
    public FirefoxOptions mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    public void addPreference(final String key, final Object value) {
        capabilities.addPreference(key, value instanceof Boolean || value instanceof Integer ? value : String.valueOf(value));
    }
}
