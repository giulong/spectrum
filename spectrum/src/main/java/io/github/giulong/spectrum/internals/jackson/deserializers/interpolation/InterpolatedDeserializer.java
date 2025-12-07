package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators.Interpolator;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Config;
import io.github.giulong.spectrum.utils.Reflections;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class InterpolatedDeserializer<T> extends JsonDeserializer<T> {

    private final Configuration configuration = Configuration.getInstance();

    @SneakyThrows
    public String interpolate(final String value, final JsonParser jsonParser) {
        final Config config = configuration.getConfig();
        final String currentName = jsonParser.currentName();
        log.trace("{} is deserializing {}: {}", getClass().getSimpleName(), currentName, value);

        if (config != null) {
            final List<Interpolator> interpolators = Reflections.getFieldsValueOf(config.getInterpolators());
            final String interpolatedValue = interpolators
                    .stream()
                    .filter(Interpolator::isEnabled)
                    .sorted(comparing(Interpolator::getPriority).reversed())
                    .map(i -> i.findVariableFor(value, jsonParser))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse(value);

            log.error("Chosen value: {} -> {}", currentName, interpolatedValue);
            return interpolatedValue;
        }

        return value;
    }

    @SneakyThrows
    public String interpolate(final JsonParser jsonParser) {
        return interpolate(jsonParser.getValueAsString(), jsonParser);
    }
}
