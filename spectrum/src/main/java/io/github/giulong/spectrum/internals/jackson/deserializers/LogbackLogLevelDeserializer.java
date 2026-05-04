package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import ch.qos.logback.classic.Level;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class LogbackLogLevelDeserializer extends ValueDeserializer<Level> {

    private static final LogbackLogLevelDeserializer INSTANCE = new LogbackLogLevelDeserializer();

    public static LogbackLogLevelDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Level deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing Logback Level from value {}", value);

        return Level.toLevel(value);
    }
}
