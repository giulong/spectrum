package com.giuliolongfils.spectrum.internals.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.logging.Level;

public class UtilLogLevelDeserializer extends JsonDeserializer<Level> {

    @Override
    public Level deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Level.parse(jsonParser.getValueAsString());
    }
}
