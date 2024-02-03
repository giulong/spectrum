package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.service.DriverService;

public class Edge extends Chromium<EdgeOptions, EdgeDriverService, EdgeDriverService.Builder> {

    @Override
    public DriverService.Builder<EdgeDriverService, EdgeDriverService.Builder> getDriverServiceBuilder() {
        return new EdgeDriverService.Builder();
    }

    @Override
    public void buildCapabilities() {
        final Configuration.WebDriver webDriverConfiguration = configuration.getWebDriver();
        final Configuration.WebDriver.Edge edgeConfig = webDriverConfiguration.getEdge();

        capabilities = new EdgeOptions();
        capabilities.addArguments(edgeConfig.getArgs());

        edgeConfig.getCapabilities().forEach(capabilities::setExperimentalOption);
        setLoggingPreferencesFrom(webDriverConfiguration.getLogs());
    }
}
