package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;

public class Firefox extends Browser<FirefoxOptions> {

    @Override
    public boolean takesPartialScreenshots() {
        return false;
    }

    @Override
    public WebDriverManager getWebDriverManager() {
        return firefoxdriver();
    }

    @Override
    public void buildCapabilitiesFrom(final Configuration.WebDriver webDriverConfiguration, final Configuration.SeleniumLogs seleniumLogs) {
        final Configuration.WebDriver.Firefox firefoxConfig = webDriverConfiguration.getFirefox();
        capabilities = new FirefoxOptions();

        if (firefoxConfig.getBinary() != null) {
            capabilities.setBinary(firefoxConfig.getBinary());
        }

        capabilities.addArguments(firefoxConfig.getArgs());
        capabilities.setLogLevel(firefoxConfig.getLogLevel());
        capabilities.setAcceptInsecureCerts(true);

        firefoxConfig.getPreferences().forEach(this::addPreference);
    }

    @Override
    public WebDriver buildWebDriver() {
        return new FirefoxDriver(capabilities);
    }

    @Override
    public void mergeGridCapabilitiesFrom(final Configuration.WebDriver.Grid gridConfiguration) {
        gridConfiguration.getCapabilities().forEach(this::setCapability);
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
