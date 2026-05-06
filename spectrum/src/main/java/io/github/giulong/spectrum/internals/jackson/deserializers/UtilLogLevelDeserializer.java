package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.util.logging.Level;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class UtilLogLevelDeserializer extends ValueDeserializer<Level> {

    private static final UtilLogLevelDeserializer INSTANCE = new UtilLogLevelDeserializer();

    public static UtilLogLevelDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Level deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing java.util.logging.Level from value {}", value);

        return Level.parse(value);
    }
}
