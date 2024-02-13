package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import java.util.Map;

import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

public abstract class Chromium<T extends ChromiumOptions<T>, U extends DriverService, V extends DriverService.Builder<U, V>> extends Driver<T, U, V> {

    public void setLoggingPreferencesFrom(final Configuration.WebDriver.Logs logs) {
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, logs.getBrowser());
        logPrefs.enable(DRIVER, logs.getDriver());
        logPrefs.enable(PERFORMANCE, logs.getPerformance());
        capabilities.setCapability(LOGGING_PREFS, logPrefs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return (T) capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }
}
