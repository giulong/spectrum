package com.giuliolongfils.agitation.browsers;

import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import static io.github.bonigarcia.wdm.WebDriverManager.edgedriver;
import static org.openqa.selenium.edge.EdgeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class Edge extends Browser<EdgeOptions> {

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
        return edgedriver();
    }

    @Override
    public String getSystemPropertyName() {
        return "webDriver.chrome.driver";
    }

    @Override
    public String getDriverName() {
        return "msedgedriver.exe";
    }

    @Override
    public void buildCapabilitiesFrom(Configuration configuration, SystemProperties systemProperties) {
        capabilities = new EdgeOptions();
        final Configuration.WebDriver.Edge edgeConfig = configuration.getWebDriver().getEdge();
        edgeConfig.getCapabilities().forEach(capabilities::setCapability);

        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, configuration.getSeleniumLogs().getBrowser());
        capabilities.setCapability(LOGGING_PREFS, logPrefs);
    }

    @Override
    public WebDriver buildWebDriver() {
        return new EdgeDriver(capabilities);
    }

    @Override
    public void mergeGridCapabilitiesFrom(final Configuration.WebDriver.Grid gridConfiguration) {
        gridConfiguration.getCapabilities().forEach(capabilities::setCapability);
    }
}
