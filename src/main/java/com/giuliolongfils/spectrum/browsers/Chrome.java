package com.giuliolongfils.spectrum.browsers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.SystemProperties;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

public class Chrome extends Browser<ChromeOptions> {

    @Override
    public boolean exposesConsole() {
        return true;
    }

    @Override
    public boolean takesPartialScreenshots() {
        return true;
    }

    @Override
    public WebDriverManager getWebDriverManager() {
        return chromedriver();
    }

    @Override
    public String getSystemPropertyName() {
        return "webDriver.chrome.driver";
    }

    @Override
    public String getDriverName() {
        return "chromedriver.exe";
    }

    @Override
    public void buildCapabilitiesFrom(Configuration configuration, SystemProperties systemProperties) {
        capabilities = new ChromeOptions();
        final Configuration.WebDriver.Chrome chromeConfig = configuration.getWebDriver().getChrome();

        final List<String> arguments = chromeConfig.getArguments();

        capabilities.addArguments(arguments);
        capabilities.setAcceptInsecureCerts(true);

        chromeConfig.getCapabilities().forEach(capabilities::setCapability);
        final LoggingPreferences logPrefs = new LoggingPreferences();
        final Configuration.SeleniumLogs seleniumLogs = configuration.getSeleniumLogs();
        logPrefs.enable(BROWSER, seleniumLogs.getBrowser());
        logPrefs.enable(DRIVER, seleniumLogs.getDriver());
        logPrefs.enable(PERFORMANCE, seleniumLogs.getPerformance());
        capabilities.setCapability(LOGGING_PREFS, logPrefs);

        final Map<String, Object> experimentalOptions = chromeConfig.getExperimentalOptions();
        experimentalOptions.forEach(capabilities::setExperimentalOption);

        @SuppressWarnings("unchecked") final Map<String, Object> prefs = (Map<String, Object>) experimentalOptions.getOrDefault("prefs", new HashMap<>());
        prefs.put("download.default_directory", configuration.getApplication().getDownloadsFolder());
    }

    @Override
    public WebDriver buildWebDriver() {
        return new ChromeDriver(capabilities);
    }

    @Override
    public void mergeGridCapabilitiesFrom(final Configuration.WebDriver.Grid gridConfiguration) {
        gridConfiguration.getCapabilities().forEach(capabilities::setCapability);
    }
}
