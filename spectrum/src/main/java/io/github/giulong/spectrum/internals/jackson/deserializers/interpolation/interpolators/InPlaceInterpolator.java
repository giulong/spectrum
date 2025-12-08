package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;

import io.github.giulong.spectrum.utils.Vars;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InPlaceInterpolator extends Interpolator {

    private static final Pattern PATTERN = Pattern.compile("(?<placeholder>\\$\\{(?<varName>[\\w.]+)(:-(?<defaultValue>[\\w~\\s-.:/\\\\=]*))?})");

    private final Vars vars = Vars.getInstance();

    @Override
    @SneakyThrows
    public Optional<String> findVariableFor(final String value, final JsonParser jsonParser) {
        final String currentName = jsonParser.currentName();
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
                    interpolatedValue = findVariableFor(interpolatedValue, jsonParser).orElse(value);
                }
            }
        }

        return interpolatedValue.equals(value) ? Optional.empty() : Optional.of(interpolatedValue);
    }
}
