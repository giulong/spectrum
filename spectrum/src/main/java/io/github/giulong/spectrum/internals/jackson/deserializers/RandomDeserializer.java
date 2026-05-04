package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.util.Random;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class RandomDeserializer extends ValueDeserializer<Random> {

    private static final RandomDeserializer INSTANCE = new RandomDeserializer();

    public static RandomDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Random deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        final long value = jsonParser.getValueAsLong();
        log.trace("Deserializing random from value {}", value);

        return new Random(value);
    }
}
