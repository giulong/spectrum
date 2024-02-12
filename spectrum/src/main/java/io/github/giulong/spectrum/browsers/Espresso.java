package io.github.giulong.spectrum.browsers;

import io.appium.java_client.android.options.EspressoOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class Espresso extends Android<EspressoOptions> {

    @Override
    public void buildCapabilities() {
        capabilities = new EspressoOptions(adjustCapabilitiesFrom(configuration
                .getWebDriver()
                .getEspresso()
                .getCapabilities()));
    }

    @Override
    public EspressoOptions mergeGridCapabilitiesFrom(final Map<String, Object> gridCapabilities) {
        return capabilities.merge(new DesiredCapabilities(gridCapabilities));
    }
}
