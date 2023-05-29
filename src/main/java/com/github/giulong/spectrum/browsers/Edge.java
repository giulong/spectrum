package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.edgedriver;

public class Edge extends Chromium<EdgeOptions> {

    @Override
    public WebDriverManager getWebDriverManager() {
        return edgedriver();
    }

    @Override
    public String getDriverName() {
        return "msedgedriver.exe";
    }

    @Override
    public void buildCapabilitiesFrom(Configuration configuration) {
        capabilities = new EdgeOptions();
        final Configuration.WebDriver.Edge edgeConfig = configuration.getWebDriver().getEdge();

        capabilities.setAcceptInsecureCerts(true);

        edgeConfig.getCapabilities().forEach(capabilities::setCapability);
        setLoggingPreferencesFrom(configuration.getSeleniumLogs());
    }

    @Override
    public WebDriver buildWebDriver() {
        return new EdgeDriver(capabilities);
    }
}
