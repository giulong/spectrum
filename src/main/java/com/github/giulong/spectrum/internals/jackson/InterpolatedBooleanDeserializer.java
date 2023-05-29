package com.github.giulong.spectrum.internals.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InterpolatedBooleanDeserializer extends InterpolatedDeserializer<Boolean> {

    @Override
    public Boolean deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        return Boolean.parseBoolean(interpolate(jsonParser.getValueAsString(), jsonParser.currentName()));
    }
}
