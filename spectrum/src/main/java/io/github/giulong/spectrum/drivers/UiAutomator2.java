package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.UiAutomator2Options;

public class UiAutomator2 extends Android<UiAutomator2Options> {

    @Override
    void buildCapabilities() {
        capabilities = new UiAutomator2Options(adjustCapabilitiesFrom(configuration
                .getDrivers()
                .getUiAutomator2()
                .getCapabilities()));
    }
}
