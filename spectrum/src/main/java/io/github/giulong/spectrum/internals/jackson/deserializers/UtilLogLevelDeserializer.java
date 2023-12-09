package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.logging.Level;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UtilLogLevelDeserializer extends JsonDeserializer<Level> {

    private static final UtilLogLevelDeserializer INSTANCE = new UtilLogLevelDeserializer();

    public static UtilLogLevelDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Level deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Level.parse(jsonParser.getValueAsString());
    }
}
