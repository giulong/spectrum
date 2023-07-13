package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.service.DriverService;

import static io.github.bonigarcia.wdm.WebDriverManager.edgedriver;

public class Edge extends Chromium<EdgeOptions, EdgeDriverService, EdgeDriverService.Builder> {

    @Override
    public DriverService.Builder<EdgeDriverService, EdgeDriverService.Builder> getDriverServiceBuilder() {
        return new EdgeDriverService.Builder();
    }

    @Override
    public WebDriverManager getWebDriverManager() {
        return edgedriver();
    }

    @Override
    public synchronized void buildCapabilitiesFrom(final Configuration.WebDriver webDriverConfiguration, final Configuration.SeleniumLogs seleniumLogs) {
        final Configuration.WebDriver.Edge edgeConfig = webDriverConfiguration.getEdge();

        capabilities = new EdgeOptions();
        capabilities.addArguments(edgeConfig.getArgs());

        edgeConfig.getCapabilities().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(seleniumLogs);
    }
}
