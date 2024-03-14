package io.github.giulong.spectrum.drivers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ThreadGuard;

import java.util.Map;

@Getter
@Slf4j
public abstract class Driver<T extends MutableCapabilities, U extends DriverService, V extends DriverService.Builder<U, V>> {

    protected static final ThreadLocal<WebDriver> WEB_DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    @JsonIgnore
    protected final Configuration configuration = Configuration.getInstance();

    @JsonPropertyDescription("WebDriver's specific capabilities")
    protected T capabilities;

    public abstract DriverService.Builder<U, V> getDriverServiceBuilder();

    public abstract void buildCapabilities();

    @SuppressWarnings("unchecked")
    public T mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return (T) capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    public synchronized WebDriver build() {
        buildCapabilities();

        final WebDriver webDriver = configuration.getRuntime().getEnvironment().setupFor(this);

        configureWaitsOf(webDriver, configuration.getDrivers().getWaits());

        WEB_DRIVER_THREAD_LOCAL.set(ThreadGuard.protect(webDriver));
        log.debug("Capabilities: {}", capabilities.toJson());

        return WEB_DRIVER_THREAD_LOCAL.get();
    }

    public void configureWaitsOf(final WebDriver webDriver, final Configuration.Drivers.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit())
                .pageLoadTimeout(waits.getPageLoadTimeout())
                .scriptTimeout(waits.getScriptTimeout());
    }

    public void shutdown() {
        WEB_DRIVER_THREAD_LOCAL.get().quit();
    }
}
