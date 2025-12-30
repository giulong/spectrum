package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import ch.qos.logback.classic.Level;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class LogbackLogLevelDeserializer extends JsonDeserializer<Level> {

    private static final LogbackLogLevelDeserializer INSTANCE = new LogbackLogLevelDeserializer();

    public static LogbackLogLevelDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Level deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing Logback Level from value {}", value);

        return Level.toLevel(value);
    }
}
