package io.github.giulong.spectrum.browsers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import java.util.Map;

public class Android extends Browser<UiAutomator2Options, AppiumDriverLocalService, AppiumServiceBuilder> {

    @Override
    public DriverService.Builder<AppiumDriverLocalService, AppiumServiceBuilder> getDriverServiceBuilder() {
        return new AppiumServiceBuilder();
    }

    @Override
    public void buildCapabilities() {
        capabilities = new UiAutomator2Options(configuration
                .getWebDriver()
                .getAndroid()
                .getCapabilities());
    }

    @Override
    public UiAutomator2Options mergeGridCapabilitiesFrom(final Map<String, String> gridCapabilities) {
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
