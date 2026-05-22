package io.github.giulong.spectrum.drivers;

import java.net.URL;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

import org.openqa.selenium.MutableCapabilities;

public class AppiumGeneric extends Appium<MutableCapabilities, AppiumDriver> {

    @Override
    public AppiumDriver buildDriverFor(final URL url) {
        return new AppiumDriver(url, capabilities);
    }

    @Override
    public AppiumGeneric buildCapabilities() {
        capabilities = new MutableCapabilities();

        return this;
    }

    @Override
    public Driver<MutableCapabilities, AppiumDriverLocalService, AppiumServiceBuilder> mergeCapabilitiesWith(final Drivers drivers) {
        capabilities.merge(new MutableCapabilities(drivers.getAppiumGeneric().getCapabilities()));

        return this;
    }
}
