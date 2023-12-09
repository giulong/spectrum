package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class InterpolatedBooleanDeserializer extends InterpolatedDeserializer<Boolean> {

    private static final InterpolatedBooleanDeserializer INSTANCE = new InterpolatedBooleanDeserializer();

    public static InterpolatedBooleanDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Boolean deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing Boolean from value {}", value);

        return Boolean.parseBoolean(interpolate(value, jsonParser.currentName()));
    }
}
