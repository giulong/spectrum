package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class UiAutomator2 extends Android<UiAutomator2Options> {

    @Override
    public void buildCapabilities() {
        capabilities = new UiAutomator2Options(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getUiAutomator2()
                .getCapabilities()));
    }

    @Override
    public UiAutomator2Options mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }
}
