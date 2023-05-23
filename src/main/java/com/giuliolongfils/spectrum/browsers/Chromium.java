package com.giuliolongfils.spectrum.browsers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.logging.LoggingPreferences;

import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

public abstract class Chromium<T extends MutableCapabilities> extends Browser<T> {

    @Override
    public boolean exposesConsole() {
        return true;
    }

    @Override
    public boolean takesPartialScreenshots() {
        return true;
    }

    @Override
    public String getSystemPropertyName() {
        return "webDriver.chrome.driver";
    }

    public void setLoggingPreferencesFrom(final Configuration.SeleniumLogs seleniumLogs) {
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, seleniumLogs.getBrowser());
        logPrefs.enable(DRIVER, seleniumLogs.getDriver());
        logPrefs.enable(PERFORMANCE, seleniumLogs.getPerformance());
        capabilities.setCapability(LOGGING_PREFS, logPrefs);
    }


    @Override
    public void mergeGridCapabilitiesFrom(final Configuration.WebDriver.Grid grid) {
        grid.getCapabilities().forEach(capabilities::setCapability);
    }
}
