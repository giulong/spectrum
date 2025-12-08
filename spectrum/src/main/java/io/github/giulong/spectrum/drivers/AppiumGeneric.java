package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.AppiumDriver;

import org.openqa.selenium.MutableCapabilities;

public class AppiumGeneric extends Appium<MutableCapabilities, AppiumDriver> {

    @Override
    public AppiumDriver buildDriverFor(final URL url) {
        return new AppiumDriver(url, capabilities);
    }

    @Override
    void buildCapabilities() {
        capabilities = new MutableCapabilities(configuration
                .getDrivers()
                .getAppiumGeneric()
                .getCapabilities());
    }
}
