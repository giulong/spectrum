package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InterpolatedBooleanDeserializer extends InterpolatedDeserializer<Boolean> {

    @Override
    public Boolean deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing Boolean from value {}", value);

        return Boolean.parseBoolean(interpolate(value, jsonParser.currentName()));
    }
}
