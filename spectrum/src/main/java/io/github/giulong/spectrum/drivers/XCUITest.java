package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

public class XCUITest extends Appium<XCUITestOptions, IOSDriver> {

    @Override
    public IOSDriver buildDriverFor(final URL url) {
        return new IOSDriver(url, capabilities);
    }

    @Override
    public XCUITest buildCapabilities() {
        capabilities = new XCUITestOptions();

        return this;
    }

    @Override
    public Driver<XCUITestOptions, AppiumDriverLocalService, AppiumServiceBuilder> mergeCapabilitiesWith(final Drivers drivers) {
        capabilities.merge(new XCUITestOptions(adjustCapabilitiesFrom(drivers.getXcuiTest().getCapabilities())));

        return this;
    }
}
