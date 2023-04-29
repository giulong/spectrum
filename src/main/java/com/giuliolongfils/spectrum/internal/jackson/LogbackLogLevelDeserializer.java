package com.giuliolongfils.spectrum.internal.jackson;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LogbackLogLevelDeserializer extends JsonDeserializer<Level> {

    private static final List<String> SUPPORTED_LEVELS = ImmutableList.of("OFF", "TRACE", "DEBUG", "INFO", "WARN");

    @Override
    public Level deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final Level level = Level.toLevel(jsonParser.getValueAsString());

        if (!SUPPORTED_LEVELS.contains(level.levelStr)) {
            throw new RuntimeException("Supported log levels are " + Arrays.toString(SUPPORTED_LEVELS.toArray()));
        }

        return level;
    }
}
