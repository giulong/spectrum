package io.github.giulong.spectrum.generation.driver_builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxBuilder extends DriverBuilder<FirefoxDriver> {

    @Override
    public FirefoxDriver buildFrom(final String argsProperty, final String capabilitiesProperty) {
        final List<String> driverArguments = new ArrayList<>();

        Optional.ofNullable(argsProperty)
                .map(args -> args.split(","))
                .map(List::of)
                .ifPresent(driverArguments::addAll);

        final FirefoxOptions options = new FirefoxOptions()
                .addArguments(driverArguments)
                .enableBiDi();

        Optional.ofNullable(capabilitiesProperty)
                .map(capabilities -> capabilities.split(","))
                .map(List::of)
                .ifPresent(capabilities -> capabilities
                        .stream()
                        .map(c -> c.split("="))
                        .forEach(c -> options.setCapability(c[0], c[1])));

        return new FirefoxDriver(options);
    }
}
