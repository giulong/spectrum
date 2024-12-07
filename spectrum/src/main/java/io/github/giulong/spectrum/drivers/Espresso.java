package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.EspressoOptions;

public class Espresso extends Android<EspressoOptions> {

    @Override
    void buildCapabilities() {
        capabilities = new EspressoOptions(adjustCapabilitiesFrom(configuration
                .getDrivers()
                .getEspresso()
                .getCapabilities()));
    }
}
