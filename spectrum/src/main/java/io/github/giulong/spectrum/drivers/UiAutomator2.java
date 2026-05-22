package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

public class UiAutomator2 extends Android<UiAutomator2Options> {

    @Override
    public UiAutomator2 buildCapabilities() {
        return this;
    }

    @Override
    public Driver<UiAutomator2Options, AppiumDriverLocalService, AppiumServiceBuilder> mergeCapabilitiesWith(final Drivers drivers) {
        capabilities = new UiAutomator2Options(adjustCapabilitiesFrom(drivers.getUiAutomator2().getCapabilities()));

        return this;
    }
}
