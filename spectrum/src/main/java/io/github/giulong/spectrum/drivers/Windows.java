package io.github.giulong.spectrum.drivers;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.Map;

public class Windows extends Appium<WindowsOptions, WindowsDriver> {

    @Override
    public void buildCapabilities() {
        capabilities = new WindowsOptions(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getWindows()
                .getCapabilities()));
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
