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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@Slf4j
@Getter
public class AppiumEnvironment extends GridEnvironment {

    @JsonIgnore
    private AppiumDriverLocalService driverService;

    @JsonIgnore
    private boolean external;

    @Override
    public void sessionOpened() {
        final Configuration.Environments.Appium appium = configuration.getEnvironments().getAppium();
        final int port = appium.getService().getPort();

        external = isRunningOn(port);
        if (external) {
            log.info("Appium is already running at port {}", port);
            return;
        }

        log.info("Starting the Appium driver service");

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
        if (external) {
            log.debug("Appium not managed by Spectrum. Avoid stopping it");
            return;
        }

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
        if (external) {
            log.debug("Appium not managed by Spectrum. Avoid closing it");
            return;
        }

        log.debug("Closing the Appium driver service");
        driverService.close();
    }

    protected boolean isRunningOn(final int port) {
        try (final ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 50);

            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
