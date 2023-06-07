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
    public void buildCapabilitiesFrom(final Configuration.WebDriver webDriverConfiguration, final Configuration.SeleniumLogs seleniumLogs) {
        capabilities = new EdgeOptions();
        capabilities.setAcceptInsecureCerts(true);

        webDriverConfiguration
                .getEdge()
                .getCapabilities()
                .forEach(capabilities::setCapability);

        setLoggingPreferencesFrom(seleniumLogs);
    }

    @Override
    public WebDriver buildWebDriver() {
        return new EdgeDriver(capabilities);
    }
}
