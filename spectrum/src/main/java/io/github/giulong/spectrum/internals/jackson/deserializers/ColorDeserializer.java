package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import java.awt.*;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ColorDeserializer extends JsonDeserializer<Color> {

    private static final ColorDeserializer INSTANCE = new ColorDeserializer();

    public static ColorDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Color deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing color from value {}", value);

        return Color.decode(value);
    }
}
