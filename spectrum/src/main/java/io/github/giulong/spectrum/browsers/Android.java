package io.github.giulong.spectrum.browsers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
public class Android extends Browser<UiAutomator2Options, AppiumDriverLocalService, AppiumServiceBuilder> {

    public static final String APP_CAPABILITY = "app";

    @Override
    public DriverService.Builder<AppiumDriverLocalService, AppiumServiceBuilder> getDriverServiceBuilder() {
        return new AppiumServiceBuilder();
    }

    @Override
    public void buildCapabilities() {
        final Map<String, Object> configurationCapabilities = configuration
                .getWebDriver()
                .getAndroid()
                .getCapabilities();

        final Path appPath = Path.of((String) configurationCapabilities.get(APP_CAPABILITY));

        if (!appPath.isAbsolute()) {
            final String absoluteAppPath = appPath.toAbsolutePath().toString();
            log.warn("Converting app path '{}' to absolute: '{}'", appPath, absoluteAppPath);
            configurationCapabilities.put(APP_CAPABILITY, absoluteAppPath);
        }

        capabilities = new UiAutomator2Options(configurationCapabilities);
    }

    @Override
    public UiAutomator2Options mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    @Override
    public void configureWaitsOf(final WebDriver webDriver, final Configuration.WebDriver.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit());
    }
}
