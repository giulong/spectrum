package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import io.github.giulong.spectrum.utils.environments.AppiumEnvironment;
import io.github.giulong.spectrum.utils.environments.Environment;
import io.github.giulong.spectrum.utils.environments.GridEnvironment;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class EnvironmentDeserializer extends InterpolatedDeserializer<Environment> {

    private static final EnvironmentDeserializer INSTANCE = new EnvironmentDeserializer();

    public static EnvironmentDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Environment deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String interpolatedValue = interpolate(jsonParser);

        return switch (interpolatedValue) {
            case "local" -> new LocalEnvironment();
            case "grid" -> new GridEnvironment();
            case "appium" -> new AppiumEnvironment();
            default -> throw new IllegalArgumentException("Value '" + interpolatedValue + "' is not a valid environment!");
        };
    }
}
