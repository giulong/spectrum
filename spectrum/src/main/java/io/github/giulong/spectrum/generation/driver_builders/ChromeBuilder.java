package io.github.giulong.spectrum.generation.driver_builders;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeBuilder extends ChromiumBuilder<ChromeDriver, ChromeOptions> {

    @Override
    protected ChromeOptions getOptions() {
        return new ChromeOptions();
    }

    @Override
    protected ChromeDriver getDriver(final ChromeOptions options) {
        return new ChromeDriver(options);
    }
}
