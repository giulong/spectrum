package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.utils.FileUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class InterpolatedStringDeserializer extends InterpolatedDeserializer<String> {

    private static final InterpolatedStringDeserializer INSTANCE = new InterpolatedStringDeserializer();

    private final FileUtils fileUtils = FileUtils.getInstance();

    public static InterpolatedStringDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing String from value {}", value);

        final String interpolatedValue = interpolate(value, jsonParser.currentName());
        return fileUtils.interpolateTimestampFrom(interpolatedValue);
    }
}
