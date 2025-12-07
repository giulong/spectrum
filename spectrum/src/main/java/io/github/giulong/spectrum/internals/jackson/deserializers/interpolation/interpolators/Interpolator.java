package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonParser;

import lombok.Getter;

@Getter
public abstract class Interpolator {

    @SuppressWarnings("unused")
    @JacksonInject("enabledFromClient")
    @JsonPropertyDescription("Whether to enable this interpolator. Injected to true by default, so no need to explicitly set it")
    private boolean enabled;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Sets the order of evaluation of this interpolator among others. Higher priority wins.")
    private int priority;

    public abstract Optional<String> findVariableFor(String value, JsonParser jsonParser);
}
