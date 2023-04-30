package com.giuliolongfils.spectrum.internal.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.VARS;

@Slf4j
public class InterpolatedStringDeserializer extends JsonDeserializer<String> {

    public static final Pattern PATTERN = Pattern.compile("(?<placeholder>\\$\\{(?<varName>\\w*)})");

    @Override
    public String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        final Matcher matcher = PATTERN.matcher(value);

        String interpolatedValue = value;
        while (matcher.find()) {
            final String currentName = jsonParser.currentName();
            final String placeholder = matcher.group("placeholder");
            interpolatedValue = interpolatedValue.replace(placeholder, VARS.getOrDefault(matcher.group("varName"), placeholder));

            if (value.equals(interpolatedValue)) {
                log.warn("No variable found to interpolate '{}' for key '{}'", value, currentName);
            } else {
                log.trace("Interpolated value for key '{}': '{}' -> '{}'", currentName, value, interpolatedValue);
            }
        }

        return interpolatedValue;
    }
}
