package io.github.giulong.spectrum.drivers;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.URL;

public class XCUITest extends Appium<XCUITestOptions, IOSDriver> {

    @Override
    public void buildCapabilities() {
        capabilities = new XCUITestOptions(adjustCapabilitiesFrom(configuration
                .getDrivers()
                .getXcuiTest()
                .getCapabilities()));
    }

    @Override
    public IOSDriver buildDriverFor(final URL url) {
        return new IOSDriver(url, capabilities);
    }
}
