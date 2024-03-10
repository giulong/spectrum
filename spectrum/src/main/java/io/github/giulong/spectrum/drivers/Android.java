package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.AndroidDriver;
import io.github.giulong.spectrum.utils.Configuration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.URL;

public abstract class Android<T extends MutableCapabilities> extends Appium<T, AndroidDriver> {

    @Override
    public void configureWaitsOf(final WebDriver webDriver, final Configuration.Drivers.Waits waits) {
        webDriver
                .manage()
                .timeouts()
                .implicitlyWait(waits.getImplicit());
    }

    @Override
    public AndroidDriver buildDriverFor(final URL url) {
        return new AndroidDriver(url, capabilities);
    }
}
