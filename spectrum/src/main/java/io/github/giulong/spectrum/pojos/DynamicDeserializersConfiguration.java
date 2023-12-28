package io.github.giulong.spectrum.pojos;

import io.github.giulong.spectrum.internals.jackson.deserializers.DynamicDeserializer;
import lombok.Getter;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public class DynamicDeserializersConfiguration {
    private List<DynamicDeserializer<?>> dynamicDeserializers;
}
