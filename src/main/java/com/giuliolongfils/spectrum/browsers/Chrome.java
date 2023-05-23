package com.giuliolongfils.spectrum.browsers;

import com.giuliolongfils.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class Chrome extends Chromium<ChromeOptions> {

    @Override
    public WebDriverManager getWebDriverManager() {
        return chromedriver();
    }

    @Override
    public String getDriverName() {
        return "chromedriver.exe";
    }

    @Override
    public void buildCapabilitiesFrom(final Configuration configuration) {
        capabilities = new ChromeOptions();
        final Configuration.WebDriver.Chrome chromeConfig = configuration.getWebDriver().getChrome();
        final List<String> arguments = chromeConfig.getArguments();

        capabilities.addArguments(arguments);
        capabilities.setAcceptInsecureCerts(true);

        chromeConfig.getCapabilities().forEach(capabilities::setCapability);
        chromeConfig.getExperimentalOptions().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(configuration.getSeleniumLogs());
    }

    @Override
    public WebDriver buildWebDriver() {
        return new ChromeDriver(capabilities);
    }
}
