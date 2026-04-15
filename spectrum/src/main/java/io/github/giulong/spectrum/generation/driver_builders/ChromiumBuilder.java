package io.github.giulong.spectrum.generation.driver_builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumOptions;

public abstract class ChromiumBuilder<T extends WebDriver, U extends ChromiumOptions<U>> extends DriverBuilder<T> {

    protected abstract U getOptions();

    protected abstract T getDriver(U options);

    @Override
    public T buildFrom(final String argsProperty, final String capabilitiesProperty) {
        final List<String> driverArguments = new ArrayList<>();

        driverArguments.add("--disable-web-security");
        Optional.ofNullable(argsProperty)
                .map(args -> args.split(","))
                .map(List::of)
                .ifPresent(driverArguments::addAll);

        final U options = getOptions().addArguments(driverArguments);
        options.setCapability("webSocketUrl", true);

        Optional.ofNullable(capabilitiesProperty)
                .map(capabilities -> capabilities.split(","))
                .map(List::of)
                .ifPresent(capabilities -> capabilities
                        .stream()
                        .map(c -> c.split("="))
                        .forEach(c -> options.setCapability(c[0], c[1])));

        return getDriver(options);
    }
}
