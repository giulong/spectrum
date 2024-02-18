package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.drivers.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class DriverDeserializer extends InterpolatedDeserializer<Driver<?, ?, ?>> {

    private static final DriverDeserializer INSTANCE = new DriverDeserializer();

    public static DriverDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Driver<?, ?, ?> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        final String interpolatedValue = interpolate(value, jsonParser.currentName());
        log.trace("Deserializing driver from value {} -> {}", value, interpolatedValue);

        return switch (interpolatedValue) {
            case "chrome" -> new Chrome();
            case "firefox" -> new Firefox();
            case "edge" -> new Edge();
            case "uiAutomator2" -> new UiAutomator2();
            case "espresso" -> new Espresso();
            case "xcuiTest" -> new XCUITest();
            case "windows" -> new Windows();
            case "mac2" -> new Mac2();
            default -> throw new IllegalArgumentException("Value '" + interpolatedValue + "' is not a valid driver!");
        };
    }
}
