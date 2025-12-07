package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import io.github.giulong.spectrum.drivers.*;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class DriverDeserializer extends InterpolatedDeserializer<Driver<?, ?, ?>> {

    private static final DriverDeserializer INSTANCE = new DriverDeserializer();

    public static DriverDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Driver<?, ?, ?> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String interpolatedValue = interpolate(jsonParser);

        return switch (interpolatedValue) {
            case "chrome" -> new Chrome();
            case "firefox" -> new Firefox();
            case "edge" -> new Edge();
            case "safari" -> new Safari();
            case "uiAutomator2" -> new UiAutomator2();
            case "espresso" -> new Espresso();
            case "xcuiTest" -> new XCUITest();
            case "windows" -> new Windows();
            case "mac2" -> new Mac2();
            case "appiumGeneric" -> new AppiumGeneric();
            default -> throw new IllegalArgumentException("Value '" + interpolatedValue + "' is not a valid driver!");
        };
    }
}
