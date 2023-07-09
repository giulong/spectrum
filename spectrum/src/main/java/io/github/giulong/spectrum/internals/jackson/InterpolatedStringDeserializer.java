package io.github.giulong.spectrum.internals.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InterpolatedStringDeserializer extends InterpolatedDeserializer<String> {

    @Override
    public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        return interpolate(jsonParser.getValueAsString(), jsonParser.currentName());
    }
}
