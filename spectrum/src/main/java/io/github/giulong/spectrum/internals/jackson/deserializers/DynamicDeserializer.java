package io.github.giulong.spectrum.internals.jackson.deserializers;

import io.github.giulong.spectrum.utils.YamlUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

@Slf4j
@AllArgsConstructor
public class DynamicDeserializer<T> extends ValueDeserializer<T> {

    private Class<T> clazz;

    private String configFile;

    @Override
    public T deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        final JsonNode jsonNode = jsonParser.readValueAsTree();
        log.trace("Deserializing {} from {} -> {}", clazz, jsonParser.currentName(), jsonNode);

        return YamlUtils.getInstance().readDynamicDeserializable(configFile, clazz, jsonNode);
    }
}
