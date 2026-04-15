package io.github.giulong.spectrum.generation.driver_builders;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeBuilder extends ChromiumBuilder<EdgeDriver, EdgeOptions> {

    @Override
    protected EdgeOptions getOptions() {
        return new EdgeOptions();
    }

    @Override
    protected EdgeDriver getDriver(final EdgeOptions options) {
        return new EdgeDriver(options);
    }
}
