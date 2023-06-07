package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

@Slf4j
@Getter
public abstract class Browser<T extends MutableCapabilities> {

    protected T capabilities;

    public abstract boolean exposesConsole();

    public abstract boolean takesPartialScreenshots();

    public abstract WebDriverManager getWebDriverManager();

    public abstract String getSystemPropertyName();

    public abstract String getDriverName();

    public abstract void buildCapabilitiesFrom(Configuration.WebDriver webDriverConfiguration, Configuration.SeleniumLogs seleniumLogs);

    public abstract WebDriver buildWebDriver();

    public abstract void mergeGridCapabilitiesFrom(Configuration.WebDriver.Grid gridConfiguration);

    public WebDriver build(final Configuration configuration) {
        final Configuration.WebDriver webDriverConfiguration = configuration.getWebDriver();

        buildCapabilitiesFrom(webDriverConfiguration, configuration.getSeleniumLogs());
        log.debug("Capabilities: {}", capabilities.toJson());

        final Configuration.WebDriver.Waits waits = webDriverConfiguration.getWaits();
        final WebDriver webDriver = configuration
                .getRuntime()
                .getEnvironment()
                .buildFrom(configuration, this);

        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit())
                .pageLoadTimeout(waits.getPageLoadTimeout())
                .scriptTimeout(waits.getScriptTimeout());

        return webDriver;
    }
}
