package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

import org.openqa.selenium.WebDriver;

public class Windows extends Appium<WindowsOptions, WindowsDriver> {

    @Override
    public WindowsDriver buildDriverFor(final URL url) {
        return new WindowsDriver(url, capabilities);
    }

    @Override
    public Windows buildCapabilities() {
        capabilities = new WindowsOptions();

        return this;
    }

    @Override
    public Driver<WindowsOptions, AppiumDriverLocalService, AppiumServiceBuilder> mergeCapabilitiesWith(final Drivers drivers) {
        capabilities.merge(new WindowsOptions(drivers.getWindows().getCapabilities()));

        return this;
    }

    @Override
    void configureWaitsOf(final WebDriver webDriver, final Configuration.Drivers.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit());
    }
}
