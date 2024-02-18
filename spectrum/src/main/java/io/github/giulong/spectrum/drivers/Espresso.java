package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.EspressoOptions;

public class Espresso extends Android<EspressoOptions> {

    @Override
    public void buildCapabilities() {
        capabilities = new EspressoOptions(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getEspresso()
                .getCapabilities()));
    }
}
