package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.time.Duration;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class DurationDeserializer extends ValueDeserializer<Duration> {

    private static final DurationDeserializer INSTANCE = new DurationDeserializer();

    public static DurationDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Duration deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        final double value = jsonParser.getValueAsDouble();
        log.trace("Deserializing duration from value {}", value);

        return Duration.ofMillis((long) (value * 1000));
    }
}
