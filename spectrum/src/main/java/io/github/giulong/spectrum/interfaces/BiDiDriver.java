package io.github.giulong.spectrum.interfaces;

import io.github.giulong.spectrum.utils.Configuration.Drivers;

import org.openqa.selenium.MutableCapabilities;

public interface BiDiDriver<T extends MutableCapabilities> {
    default void activateBiDi(final T capabilities, final Drivers drivers, final Drivers.BiDiDriverConfiguration biDiDriverConfiguration) {
        capabilities.setCapability("webSocketUrl", drivers.isBiDi() || biDiDriverConfiguration.isBiDi());
    }
}
