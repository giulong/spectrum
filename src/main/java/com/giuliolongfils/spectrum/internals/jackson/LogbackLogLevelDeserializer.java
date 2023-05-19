package com.giuliolongfils.spectrum.internals.jackson;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class LogbackLogLevelDeserializer extends JsonDeserializer<Level> {

    @Override
    public Level deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Level.toLevel(jsonParser.getValueAsString());
    }
}
