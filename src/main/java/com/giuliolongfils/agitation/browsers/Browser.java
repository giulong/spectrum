package com.giuliolongfils.agitation.browsers;

import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@Getter
public abstract class Browser<T extends MutableCapabilities> {

    protected T capabilities;

    abstract public boolean exposesConsole();

    abstract public boolean takesPartialScreenshots();

    abstract public WebDriverManager getWebDriverManager();

    abstract public String getSystemPropertyName();

    abstract public String getDriverName();

    abstract public void buildCapabilitiesFrom(Configuration configuration, SystemProperties systemProperties);

    abstract public WebDriver buildWebDriver();

    abstract public void mergeGridCapabilitiesFrom(Configuration.WebDriver.Grid gridConfiguration);

    @SneakyThrows
    public WebDriver build(Configuration configuration, SystemProperties systemProperties) {
        buildCapabilitiesFrom(configuration, systemProperties);
        log.info("Capabilities: {}", capabilities.toJson());

        if (systemProperties.isGrid()) {
            Configuration.WebDriver.Grid gridConfiguration = configuration.getWebDriver().getGrid();
            mergeGridCapabilitiesFrom(gridConfiguration);
            return setTimeouts(RemoteWebDriver.builder().oneOf(capabilities).address(gridConfiguration.getUrl()).build(), configuration.getWebDriver());
        }

        final String driversPath = configuration.getApplication().getDriversPath();
        if (systemProperties.isDownloadWebDriver()) {
            getWebDriverManager().avoidOutputTree().cachePath(driversPath).setup();
        } else {
            log.warn("WebDriverManager disabled: using local webDriver");
            System.setProperty(getSystemPropertyName(), Paths.get(driversPath).resolve(getDriverName()).toString());
        }

        return setTimeouts(buildWebDriver(), configuration.getWebDriver());
    }

    protected WebDriver setTimeouts(final WebDriver webDriver, final Configuration.WebDriver webDriverConf) {
        webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(webDriverConf.getWaitTimeout()))
                .pageLoadTimeout(Duration.ofSeconds(webDriverConf.getPageLoadingWaitTimeout()))
                .scriptTimeout(Duration.ofSeconds(webDriverConf.getScriptWaitTimeout()));

        return webDriver;
    }
}
