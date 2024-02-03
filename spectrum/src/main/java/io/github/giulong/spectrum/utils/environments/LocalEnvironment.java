package io.github.giulong.spectrum.utils.environments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.internals.BrowserLog;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

@Slf4j
public class LocalEnvironment extends Environment {

    @JsonIgnore
    protected final Configuration configuration = Configuration.getInstance();

    protected static final ThreadLocal<DriverService> DRIVER_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public WebDriver setupFor(final Browser<?, ?, ?> browser) {
        log.info("Running in local");

        DRIVER_SERVICE_THREAD_LOCAL.set(browser
                .getDriverServiceBuilder()
                .withLogOutput(BrowserLog
                        .builder()
                        .level(configuration.getWebDriver().getLogs().getLevel())
                        .build())
                .build());

        return RemoteWebDriver
                .builder()
                .withDriverService(DRIVER_SERVICE_THREAD_LOCAL.get())
                .oneOf(browser.getCapabilities())
                .build();
    }

    @Override
    public void shutdown() {
        DRIVER_SERVICE_THREAD_LOCAL.get().close();
    }
}
