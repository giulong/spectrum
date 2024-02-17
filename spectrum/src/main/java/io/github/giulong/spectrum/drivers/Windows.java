package io.github.giulong.spectrum.drivers;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.Map;

public class Windows extends Appium<WindowsOptions, WindowsDriver> {

    @Override
    public void configureWaitsOf(final WebDriver webDriver, final Configuration.WebDriver.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit());
    }

    @Override
    public void buildCapabilities() {
        capabilities = new WindowsOptions(configuration
                .getWebDriver()
                .getWindows()
                .getCapabilities());
    }

    @Override
    public WindowsOptions mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    @Override
    public WindowsDriver buildDriverFor(final URL url) {
        return new WindowsDriver(url, capabilities);
    }
}
