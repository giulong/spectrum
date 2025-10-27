package io.github.giulong.spectrum.interfaces;

import io.github.giulong.spectrum.utils.Configuration;

import org.openqa.selenium.MutableCapabilities;

public interface BiDiDriver<T extends MutableCapabilities> {
    default void activateBiDi(final T capabilities, final Configuration configuration, final Configuration.Drivers.BiDiDriverConfiguration biDiDriverConfiguration) {
        capabilities.setCapability("webSocketUrl", configuration.getDrivers().isBiDi() || biDiDriverConfiguration.isBiDi());
    }
}
