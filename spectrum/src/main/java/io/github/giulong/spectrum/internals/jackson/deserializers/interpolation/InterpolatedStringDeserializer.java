package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import io.github.giulong.spectrum.utils.FileUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class InterpolatedStringDeserializer extends InterpolatedDeserializer<String> {

    private static final InterpolatedStringDeserializer INSTANCE = new InterpolatedStringDeserializer();

    private final FileUtils fileUtils = FileUtils.getInstance();

    public static InterpolatedStringDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        return fileUtils.interpolateTimestampFrom(interpolate(jsonParser));
    }
}
