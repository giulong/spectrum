package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.UiAutomator2Options;

public class UiAutomator2 extends Android<UiAutomator2Options> {

    @Override
    public void buildCapabilities() {
        capabilities = new UiAutomator2Options(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getUiAutomator2()
                .getCapabilities()));
    }
}
