package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

@NoArgsConstructor(access = PRIVATE)
public class InterpolatedBooleanDeserializer extends InterpolatedDeserializer<Boolean> {

    private static final InterpolatedBooleanDeserializer INSTANCE = new InterpolatedBooleanDeserializer();

    public static InterpolatedBooleanDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Boolean deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        return Boolean.parseBoolean(interpolate(jsonParser));
    }
}
