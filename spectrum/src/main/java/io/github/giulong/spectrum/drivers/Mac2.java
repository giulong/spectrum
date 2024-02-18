package io.github.giulong.spectrum.drivers;

import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.Map;

public class Mac2 extends Appium<Mac2Options, Mac2Driver> {

    @Override
    public void buildCapabilities() {
        capabilities = new Mac2Options(configuration
                .getWebDriver()
                .getMac2()
                .getCapabilities());
    }

    @Override
    public Mac2Options mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    @Override
    public Mac2Driver buildDriverFor(final URL url) {
        return new Mac2Driver(url, capabilities);
    }
}
