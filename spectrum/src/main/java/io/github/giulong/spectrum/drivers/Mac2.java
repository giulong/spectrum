package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;

public class Mac2 extends Appium<Mac2Options, Mac2Driver> {

    @Override
    public Mac2Driver buildDriverFor(final URL url) {
        return new Mac2Driver(url, capabilities);
    }

    @Override
    void buildCapabilities() {
        capabilities = new Mac2Options(configuration
                .getDrivers()
                .getMac2()
                .getCapabilities());
    }
}
