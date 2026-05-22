package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.EspressoOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration.Drivers;

public class Espresso extends Android<EspressoOptions> {

    @Override
    public Espresso buildCapabilities() {
        capabilities = new EspressoOptions();

        return this;
    }

    @Override
    public Driver<EspressoOptions, AppiumDriverLocalService, AppiumServiceBuilder> mergeCapabilitiesWith(final Drivers drivers) {
        capabilities.merge(new EspressoOptions(adjustCapabilitiesFrom(drivers.getEspresso().getCapabilities())));

        return this;
    }
}
