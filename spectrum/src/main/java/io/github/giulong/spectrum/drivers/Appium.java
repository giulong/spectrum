package io.github.giulong.spectrum.drivers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
public abstract class Appium<T extends MutableCapabilities, U extends AppiumDriver> extends Driver<T, AppiumDriverLocalService, AppiumServiceBuilder> {

    public static final String APP_CAPABILITY = "app";

    public abstract U buildDriverFor(URL url);

    @Override
    public DriverService.Builder<AppiumDriverLocalService, AppiumServiceBuilder> getDriverServiceBuilder() {
        return new AppiumServiceBuilder();
    }

    protected Map<String, Object> adjustCapabilitiesFrom(final Map<String, Object> configurationCapabilities) {
        log.debug("Adjusting capabilities for {}", getClass().getSimpleName());
        final Path appPath = Path.of((String) configurationCapabilities.get(APP_CAPABILITY));

        if (!appPath.isAbsolute()) {
            final String absoluteAppPath = appPath.toAbsolutePath().toString();
            log.warn("Converting app path '{}' to absolute: '{}'", appPath, absoluteAppPath);
            configurationCapabilities.put(APP_CAPABILITY, absoluteAppPath);
        }

        return configurationCapabilities;
    }
}
