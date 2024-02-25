package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;

public class Chrome extends Chromium<ChromeOptions, ChromeDriverService, ChromeDriverService.Builder> {

    @Override
    public DriverService.Builder<ChromeDriverService, ChromeDriverService.Builder> getDriverServiceBuilder() {
        return new ChromeDriverService.Builder();
    }

    @Override
    public void buildCapabilities() {
        final Configuration.WebDriver webDriverConfiguration = configuration.getWebDriver();
        final Configuration.WebDriver.Chrome chromeConfig = webDriverConfiguration.getChrome();

        capabilities = new ChromeOptions().addArguments(chromeConfig.getArgs());

        chromeConfig.getCapabilities().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(webDriverConfiguration.getLogs());
    }
}
