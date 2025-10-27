package io.github.giulong.spectrum.utils.environments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.DriverLog;
import io.github.giulong.spectrum.utils.Configuration;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

@Slf4j
public class LocalEnvironment extends Environment {

    @JsonIgnore
    private final Configuration configuration = Configuration.getInstance();

    protected static final ThreadLocal<DriverService> DRIVER_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public WebDriver setupFor(final Driver<?, ?, ?> driver) {
        log.info("Running in local");

        DRIVER_SERVICE_THREAD_LOCAL.set(driver
                .getDriverServiceBuilder()
                .withLogOutput(DriverLog
                        .builder()
                        .level(configuration.getDrivers().getLogs().getLevel())
                        .build())
                .build());

        return RemoteWebDriver
                .builder()
                .withDriverService(DRIVER_SERVICE_THREAD_LOCAL.get())
                .oneOf(driver.getCapabilities())
                .build();
    }

    @Override
    public void shutdown() {
        DRIVER_SERVICE_THREAD_LOCAL.get().close();
    }
}
