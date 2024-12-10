package io.github.giulong.spectrum.drivers;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.WebDriver;

import java.net.URL;

public class Windows extends Appium<WindowsOptions, WindowsDriver> {

    @Override
    public WindowsDriver buildDriverFor(final URL url) {
        return new WindowsDriver(url, capabilities);
    }

    @Override
    void buildCapabilities() {
        capabilities = new WindowsOptions(configuration
                .getDrivers()
                .getWindows()
                .getCapabilities());
    }

    @Override
    void configureWaitsOf(final WebDriver webDriver, final Configuration.Drivers.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit());
    }
}
