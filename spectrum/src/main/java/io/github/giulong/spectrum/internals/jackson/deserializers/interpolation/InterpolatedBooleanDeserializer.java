package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class InterpolatedBooleanDeserializer extends InterpolatedDeserializer<Boolean> {

    private static final InterpolatedBooleanDeserializer INSTANCE = new InterpolatedBooleanDeserializer();

    public static InterpolatedBooleanDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Boolean deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        return Boolean.parseBoolean(interpolate(jsonParser));
    }
}
