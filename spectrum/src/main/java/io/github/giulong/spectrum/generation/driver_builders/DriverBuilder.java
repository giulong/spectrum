package io.github.giulong.spectrum.generation.driver_builders;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;

@Slf4j
public abstract class DriverBuilder<T extends WebDriver> {

    public abstract T buildFrom(String argsProperty, String capabilitiesProperty);

    public static DriverBuilder<?> getFor(final String name) {
        final DriverBuilder<?> driverBuilder = switch (name.toLowerCase()) {
            case "chrome" -> new ChromeBuilder();
            case "edge" -> new EdgeBuilder();
            default -> throw new IllegalArgumentException("Value '" + name + "' is not a valid driver!");
        };

        log.debug("Returning an instance of {}", driverBuilder.getClass().getSimpleName());
        return driverBuilder;
    }
}
