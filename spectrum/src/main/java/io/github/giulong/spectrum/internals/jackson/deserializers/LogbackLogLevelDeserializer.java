package io.github.giulong.spectrum.internals.jackson.deserializers;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NoArgsConstructor;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LogbackLogLevelDeserializer extends JsonDeserializer<Level> {

    private static final LogbackLogLevelDeserializer INSTANCE = new LogbackLogLevelDeserializer();

    public static LogbackLogLevelDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Level deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Level.toLevel(jsonParser.getValueAsString());
    }
}
