package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class UtilLogLevelDeserializer extends JsonDeserializer<Level> {

    private static final UtilLogLevelDeserializer INSTANCE = new UtilLogLevelDeserializer();

    public static UtilLogLevelDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Level deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing java.util.logging.Level from value {}", value);

        return Level.parse(value);
    }
}
