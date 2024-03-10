package io.github.giulong.spectrum.utils.environments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.drivers.Appium;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.AppiumLog;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
@Getter
public class AppiumEnvironment extends GridEnvironment {

    @JsonIgnore
    private AppiumDriverLocalService driverService;

    @Override
    public void sessionOpened() {
        log.info("Starting the Appium driver service");

        final Configuration.Environments.Appium appium = configuration.getEnvironments().getAppium();
        final AppiumServiceBuilder appiumServiceBuilder = ((AppiumServiceBuilder) configuration
                .getRuntime()
                .getDriver()
                .getDriverServiceBuilder())
                .withCapabilities(new DesiredCapabilities(appium.getCapabilities()));

        driverService = AppiumDriverLocalService.buildService(appiumServiceBuilder);

        if (appium.isCollectServerLogs()) {
            driverService.clearOutPutStreams();
            driverService.addOutPutStream(AppiumLog
                    .builder()
                    .level(configuration.getDrivers().getLogs().getLevel())
                    .build());
        }

        driverService.start();
    }

    @Override
    public void sessionClosed() {
        log.debug("Stopping the Appium driver service");

        driverService.stop();
    }

    @Override
    public WebDriver setupFor(final Driver<?, ?, ?> driver) {
        log.info("Running with appium");
        final Configuration.Environments.Appium appium = configuration.getEnvironments().getAppium();
        final RemoteWebDriver webDriver = ((Appium<?, ?>) driver).buildDriverFor(appium.getUrl());

        return setFileDetectorFor(webDriver, appium);
    }

    @Override
    public void shutdown() {
        log.debug("Closing the Appium driver service");

        driverService.close();
    }
}
