package io.github.giulong.spectrum.utils.webdrivers;

import io.github.giulong.spectrum.browsers.Browser;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.remote.service.DriverService;

@Slf4j
public class LocalEnvironment extends Environment {

    protected static final ThreadLocal<DriverService> DRIVER_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public WebDriver setupFrom(final Browser<?, ?, ?> browser, final RemoteWebDriverBuilder webDriverBuilder) {
        log.info("Running in local");

        DRIVER_SERVICE_THREAD_LOCAL.set(browser
                .getDriverServiceBuilder()
                .withLogOutput(System.out)
                .build());

        return webDriverBuilder
                .withDriverService(DRIVER_SERVICE_THREAD_LOCAL.get())
                .build();
    }

    @Override
    public void shutdown() {
        DRIVER_SERVICE_THREAD_LOCAL.get().close();
    }
}
