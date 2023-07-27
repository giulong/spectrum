package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.pojos.Configuration;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverService;

import java.util.Map;

public class Firefox extends Browser<FirefoxOptions, GeckoDriverService, GeckoDriverService.Builder> {

    @Override
    public DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> getDriverServiceBuilder() {
        return new GeckoDriverService.Builder();
    }

    @Override
    public void buildCapabilitiesFrom(final Configuration.WebDriver webDriverConfiguration) {
        final Configuration.WebDriver.Firefox firefoxConfig = webDriverConfiguration.getFirefox();

        capabilities = new FirefoxOptions();
        capabilities.addArguments(firefoxConfig.getArgs());
        capabilities.setLogLevel(firefoxConfig.getLogLevel());

        firefoxConfig.getPreferences().forEach(this::addPreference);
    }

    @Override
    public void mergeGridCapabilitiesFrom(final Map<String, String> gridCapabilities) {
        gridCapabilities.forEach(this::setCapability);
    }

    public void addPreference(final String key, final Object value) {
        capabilities.addPreference(key, value instanceof Boolean || value instanceof Integer ? value : String.valueOf(value));
    }

    public void setCapability(final String key, final Object value) {
        if (value instanceof Boolean) {
            capabilities.setCapability(key, (boolean) value);
        } else if (value instanceof String) {
            capabilities.setCapability(key, String.valueOf(value));
        } else {
            capabilities.setCapability(key, value);
        }
    }
}
