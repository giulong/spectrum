package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import static com.fasterxml.jackson.annotation.OptBoolean.TRUE;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Getter;

import tools.jackson.core.JsonParser;

@Getter
public abstract class Interpolator {

    @SuppressWarnings("unused")
    @JacksonInject(value = "enabledFromClient", optional = TRUE)
    @JsonPropertyDescription("Whether to enable this interpolator. Injected to true by default, so no need to explicitly set it")
    private boolean enabled;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Sets the order of evaluation of this interpolator among others. Higher priority wins.")
    private int priority;

    public abstract Optional<String> findVariableFor(String value, JsonParser jsonParser);
}
