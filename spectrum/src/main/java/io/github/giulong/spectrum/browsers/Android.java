package io.github.giulong.spectrum.browsers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

@Slf4j
public class Android extends Appium<UiAutomator2Options> {

    @Override
    public void buildCapabilities() {
        capabilities = new UiAutomator2Options(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getAndroid()
                .getCapabilities()));
    }

    @Override
    public UiAutomator2Options mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }

    @Override
    public void configureWaitsOf(final WebDriver webDriver, final Configuration.WebDriver.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit());
    }
}
