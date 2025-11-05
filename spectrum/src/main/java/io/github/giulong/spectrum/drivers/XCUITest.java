package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

public class XCUITest extends Appium<XCUITestOptions, IOSDriver> {

    @Override
    public IOSDriver buildDriverFor(final URL url) {
        return new IOSDriver(url, capabilities);
    }

    @Override
    void buildCapabilities() {
        capabilities = new XCUITestOptions(adjustCapabilitiesFrom(configuration
                .getDrivers()
                .getXcuiTest()
                .getCapabilities()));
    }
}
