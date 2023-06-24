package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class Chrome extends Chromium<ChromeOptions> {

    @Override
    public WebDriverManager getWebDriverManager() {
        return chromedriver();
    }

    @Override
    public void buildCapabilitiesFrom(final Configuration.WebDriver webDriverConfiguration, final Configuration.SeleniumLogs seleniumLogs) {
        final Configuration.WebDriver.Chrome chromeConfig = webDriverConfiguration.getChrome();

        capabilities = new ChromeOptions();
        capabilities.addArguments(chromeConfig.getArgs());

        chromeConfig.getCapabilities().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(seleniumLogs);
    }
}
