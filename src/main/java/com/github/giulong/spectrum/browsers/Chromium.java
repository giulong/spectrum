package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.Map;

import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

public abstract class Chromium<T extends ChromiumOptions<T>> extends Browser<T> {

    public void setLoggingPreferencesFrom(final Configuration.SeleniumLogs seleniumLogs) {
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, seleniumLogs.getBrowser());
        logPrefs.enable(DRIVER, seleniumLogs.getDriver());
        logPrefs.enable(PERFORMANCE, seleniumLogs.getPerformance());
        capabilities.setCapability(LOGGING_PREFS, logPrefs);
    }

    @Override
    public void mergeGridCapabilitiesFrom(final Map<String, String> gridCapabilities) {
        gridCapabilities.forEach(this.capabilities::setCapability);
    }
}
