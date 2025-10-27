package io.github.giulong.spectrum.internals.jackson.deserializers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonDeserializer;

import io.github.giulong.spectrum.utils.Vars;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class InterpolatedDeserializer<T> extends JsonDeserializer<T> {

    private static final Pattern PATTERN = Pattern.compile("(?<placeholder>\\$\\{(?<varName>[\\w.]+)(:-(?<defaultValue>[\\w~\\s-.:/\\\\=]*))?})");

    private final Vars vars = Vars.getInstance();

    public String interpolate(final String value, final String currentName) {
        final Matcher matcher = PATTERN.matcher(value);

        String interpolatedValue = value;
        while (matcher.find()) {
            final String varName = matcher.group("varName");
            final String placeholder = matcher.group("placeholder");
            final String defaultValue = matcher.group("defaultValue");
            final String envVar = System.getenv(varName);
            final String envVarOrPlaceholder = envVar != null ? envVar : placeholder;
            final String systemProperty = System.getProperty(varName, envVarOrPlaceholder);

            interpolatedValue = interpolatedValue.replace(placeholder, String.valueOf(vars.getOrDefault(varName, systemProperty)));

            if (value.equals(interpolatedValue)) {
                if (defaultValue == null) {
                    log.debug("No variable found to interpolate '{}' for key '{}'", value, currentName);
                } else {
                    log.trace("No variable found to interpolate '{}' for key '{}'. Using provided default '{}'", value, currentName, defaultValue);
                    interpolatedValue = value.replace(placeholder, defaultValue);
                }
            } else {
                log.trace("Interpolated value for key '{}: {}' -> '{}'", currentName, value, interpolatedValue);

                if (PATTERN.matcher(interpolatedValue).find()) {
                    interpolatedValue = interpolate(interpolatedValue, currentName);
                }
            }
        }

        return interpolatedValue;
    }
}
