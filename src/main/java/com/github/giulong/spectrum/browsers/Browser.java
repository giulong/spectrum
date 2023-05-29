package com.github.giulong.spectrum.browsers;

import com.github.giulong.spectrum.pojos.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.file.Paths;

@Slf4j
@Getter
public abstract class Browser<T extends MutableCapabilities> {

    protected T capabilities;

    public abstract boolean exposesConsole();

    public abstract boolean takesPartialScreenshots();

    public abstract WebDriverManager getWebDriverManager();

    public abstract String getSystemPropertyName();

    public abstract String getDriverName();

    public abstract void buildCapabilitiesFrom(Configuration configuration);

    public abstract WebDriver buildWebDriver();

    public abstract void mergeGridCapabilitiesFrom(Configuration.WebDriver.Grid gridConfiguration);

    public WebDriver build(final Configuration configuration) {
        buildCapabilitiesFrom(configuration);
        log.info("Capabilities: {}", capabilities.toJson());

        final Configuration.Runtime runtime = configuration.getRuntime();
        if (runtime.isGrid()) {
            Configuration.WebDriver.Grid gridConfiguration = configuration.getWebDriver().getGrid();
            mergeGridCapabilitiesFrom(gridConfiguration);
            return setTimeouts(RemoteWebDriver
                            .builder()
                            .oneOf(capabilities)
                            .address(gridConfiguration.getUrl()).build(),
                    configuration.getWebDriver().getWaits());
        }

        final String driversPath = runtime.getDriversPath();
        if (runtime.isDownloadWebDriver()) {
            final WebDriverManager webDriverManager = getWebDriverManager().avoidOutputTree().cachePath(driversPath);

            if (runtime.isDocker()) {
                log.info("Running in Docker");
                webDriverManager.browserInDocker();
            }

            webDriverManager.setup();
        } else {
            log.warn("WebDriverManager disabled: using local webDriver");
            System.setProperty(getSystemPropertyName(), Paths.get(driversPath).resolve(getDriverName()).toString());
        }

        return setTimeouts(buildWebDriver(), configuration.getWebDriver().getWaits());
    }

    protected WebDriver setTimeouts(final WebDriver webDriver, final Configuration.WebDriver.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit())
                .pageLoadTimeout(waits.getPageLoadTimeout())
                .scriptTimeout(waits.getScriptTimeout());

        return webDriver;
    }
}
