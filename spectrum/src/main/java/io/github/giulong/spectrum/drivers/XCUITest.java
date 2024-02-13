package io.github.giulong.spectrum.drivers;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.Map;

public class XCUITest extends Appium<XCUITestOptions, IOSDriver> {

    @Override
    public void buildCapabilities() {
        capabilities = new XCUITestOptions(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getXcuiTest()
                .getCapabilities()));
    }

    @Override
    public XCUITestOptions mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    @Override
    public IOSDriver buildDriverFor(final URL url) {
        return new IOSDriver(url, capabilities);
    }
}
