package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.browsers.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BrowserDeserializer extends InterpolatedDeserializer<Browser<?, ?, ?>> {

    @Override
    public Browser<?, ?, ?> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        final String interpolatedValue = interpolate(value, jsonParser.currentName());
        log.trace("Deserializing browser from value {} -> {}", value, interpolatedValue);

        return switch (interpolatedValue) {
            case "chrome" -> new Chrome();
            case "firefox" -> new Firefox();
            case "edge" -> new Edge();
            default -> throw new IllegalArgumentException("Value '" + interpolatedValue + "' is not a valid browser!");
        };
    }
}
