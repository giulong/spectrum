package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static lombok.AccessLevel.PRIVATE;

import io.github.giulong.spectrum.utils.FileUtils;

import lombok.NoArgsConstructor;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

@NoArgsConstructor(access = PRIVATE)
public class InterpolatedStringDeserializer extends InterpolatedDeserializer<String> {

    private static final InterpolatedStringDeserializer INSTANCE = new InterpolatedStringDeserializer();

    private final FileUtils fileUtils = FileUtils.getInstance();

    public static InterpolatedStringDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        return fileUtils.interpolateTimestampFrom(interpolate(jsonParser));
    }
}
