package com.giuliolongfils.spectrum.internals.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        return Duration.ofSeconds(jsonParser.getValueAsInt());
    }
}
