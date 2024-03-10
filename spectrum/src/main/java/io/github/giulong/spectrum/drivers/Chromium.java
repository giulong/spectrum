package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.service.DriverService;

import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

public abstract class Chromium<T extends ChromiumOptions<T>, U extends DriverService, V extends DriverService.Builder<U, V>> extends Driver<T, U, V> {

    public void setLoggingPreferencesFrom(final Configuration.Drivers.Logs logs) {
        final LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(BROWSER, logs.getBrowser());
        loggingPreferences.enable(DRIVER, logs.getDriver());
        loggingPreferences.enable(PERFORMANCE, logs.getPerformance());
        capabilities.setCapability(LOGGING_PREFS, loggingPreferences);
    }
}
