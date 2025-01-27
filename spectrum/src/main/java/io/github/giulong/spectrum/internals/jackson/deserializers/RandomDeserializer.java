package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class RandomDeserializer extends JsonDeserializer<Random> {

    private static final RandomDeserializer INSTANCE = new RandomDeserializer();

    public static RandomDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Random deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final long value = jsonParser.getValueAsLong();
        log.trace("Deserializing random from value {}", value);

        return new Random(value);
    }
}
