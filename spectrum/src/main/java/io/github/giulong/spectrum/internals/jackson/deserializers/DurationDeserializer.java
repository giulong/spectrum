package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class DurationDeserializer extends JsonDeserializer<Duration> {

    private static final DurationDeserializer INSTANCE = new DurationDeserializer();

    public static DurationDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Duration deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final double value = jsonParser.getValueAsDouble();
        log.trace("Deserializing duration from value {}", value);

        return Duration.ofMillis((long) (value * 1000));
    }
}
