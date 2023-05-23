package com.giuliolongfils.spectrum.browsers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;

public class Firefox extends Browser<FirefoxOptions> {

    @Override
    public boolean exposesConsole() {
        return true;
    }

    @Override
    public boolean takesPartialScreenshots() {
        return false;
    }

    @Override
    public WebDriverManager getWebDriverManager() {
        return firefoxdriver();
    }

    @Override
    public String getSystemPropertyName() {
        return "webDriver.gecko.driver";
    }

    @Override
    public String getDriverName() {
        return "geckodriver.exe";
    }

    @Override
    public void buildCapabilitiesFrom(Configuration configuration) {
        capabilities = new FirefoxOptions();
        final Configuration.WebDriver.Firefox firefoxConfig = configuration.getWebDriver().getFirefox();

        if (firefoxConfig.getBinary() != null) {
            capabilities.setBinary(firefoxConfig.getBinary());
        }

        capabilities.addArguments(firefoxConfig.getArgs());
        capabilities.setLogLevel(firefoxConfig.getLogLevel());
        capabilities.setAcceptInsecureCerts(true);

        firefoxConfig.getPreferences().forEach((k, v) -> addPreference(k, v, capabilities));
    }

    @Override
    public WebDriver buildWebDriver() {
        return new FirefoxDriver(capabilities);
    }

    @Override
    public void mergeGridCapabilitiesFrom(final Configuration.WebDriver.Grid gridConfiguration) {
        gridConfiguration.getCapabilities().forEach((k, v) -> setCapability(k, v, this.capabilities));
    }

    public void addPreference(final String key, final Object value, final FirefoxOptions firefoxOptions) {
        firefoxOptions.addPreference(key, value instanceof Boolean || value instanceof Integer ? value : String.valueOf(value));
    }

    public void setCapability(final String key, final Object value, final FirefoxOptions firefoxOptions) {
        if (value instanceof Boolean) {
            firefoxOptions.setCapability(key, (boolean) value);
        } else if (value instanceof String) {
            firefoxOptions.setCapability(key, String.valueOf(value));
        } else {
            firefoxOptions.setCapability(key, value);
        }
    }
}
