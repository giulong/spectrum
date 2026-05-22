package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

public class Mac2 extends Appium<Mac2Options, Mac2Driver> {

    @Override
    public Mac2Driver buildDriverFor(final URL url) {
        return new Mac2Driver(url, capabilities);
    }

    @Override
    public Mac2 buildCapabilities() {
        capabilities = new Mac2Options();

        return this;
    }

    @Override
    public Driver<Mac2Options, AppiumDriverLocalService, AppiumServiceBuilder> mergeCapabilitiesWith(final Drivers drivers) {
        capabilities.merge(new Mac2Options(drivers.getMac2().getCapabilities()));

        return this;
    }
}
