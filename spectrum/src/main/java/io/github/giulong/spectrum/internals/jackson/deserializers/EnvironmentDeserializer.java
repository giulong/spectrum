package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.giulong.spectrum.utils.environments.AppiumEnvironment;
import io.github.giulong.spectrum.utils.environments.Environment;
import io.github.giulong.spectrum.utils.environments.GridEnvironment;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class EnvironmentDeserializer extends InterpolatedDeserializer<Environment> {

    private static final EnvironmentDeserializer INSTANCE = new EnvironmentDeserializer();

    public static EnvironmentDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Environment deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        final String interpolatedValue = interpolate(value, jsonParser.currentName());
        log.trace("Deserializing environment from value {} -> {}", value, interpolatedValue);

        return switch (interpolatedValue) {
            case "local" -> new LocalEnvironment();
            case "grid" -> new GridEnvironment();
            case "appium" -> new AppiumEnvironment();
            default -> throw new IllegalArgumentException("Value '" + interpolatedValue + "' is not a valid environment!");
        };
    }
}
