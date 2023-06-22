package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.support.ThreadGuard;

@Slf4j
public abstract class Browser<T extends AbstractDriverOptions<?>> {

    public static final ThreadLocal<WebDriver> WEB_DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    protected T capabilities;

    public abstract WebDriverManager getWebDriverManager();

    public abstract void buildCapabilitiesFrom(Configuration.WebDriver webDriverConfiguration, Configuration.SeleniumLogs seleniumLogs);

    public abstract void mergeGridCapabilitiesFrom(Configuration.WebDriver.Grid gridConfiguration);

    public WebDriver build(final Configuration configuration) {
        final Configuration.WebDriver webDriverConfiguration = configuration.getWebDriver();

        buildCapabilitiesFrom(webDriverConfiguration, configuration.getSeleniumLogs());
        capabilities.setAcceptInsecureCerts(true);
        log.debug("Capabilities: {}", capabilities.toJson());

        final RemoteWebDriverBuilder webDriverBuilder = RemoteWebDriver.builder().oneOf(capabilities);

        configuration
                .getRuntime()
                .getEnvironment()
                .buildFrom(configuration, this, webDriverBuilder);

        final WebDriver webDriver = webDriverBuilder.build();
        final Configuration.WebDriver.Waits waits = webDriverConfiguration.getWaits();

        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit())
                .pageLoadTimeout(waits.getPageLoadTimeout())
                .scriptTimeout(waits.getScriptTimeout());

        WEB_DRIVER_THREAD_LOCAL.set(ThreadGuard.protect(webDriver));
        return WEB_DRIVER_THREAD_LOCAL.get();
    }
}
