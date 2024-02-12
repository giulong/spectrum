package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.browsers.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class BrowserDeserializer extends InterpolatedDeserializer<Browser<?, ?, ?>> {

    private static final BrowserDeserializer INSTANCE = new BrowserDeserializer();

    public static BrowserDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Browser<?, ?, ?> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        final String interpolatedValue = interpolate(value, jsonParser.currentName());
        log.trace("Deserializing browser from value {} -> {}", value, interpolatedValue);

        return switch (interpolatedValue) {
            case "chrome" -> new Chrome();
            case "firefox" -> new Firefox();
            case "edge" -> new Edge();
            case "uiAutomator2" -> new UiAutomator2();
            case "espresso" -> new Espresso();
            case "xcuiTest" -> new XCUITest();
            default -> throw new IllegalArgumentException("Value '" + interpolatedValue + "' is not a valid browser!");
        };
    }
}
