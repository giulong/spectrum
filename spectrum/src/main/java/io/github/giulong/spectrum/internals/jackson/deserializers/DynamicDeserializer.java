package io.github.giulong.spectrum.internals.jackson.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.giulong.spectrum.utils.YamlUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DynamicDeserializer<T> extends JsonDeserializer<T> {

    private Class<T> clazz;

    private String configFile;

    @Override
    public T deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final JsonNode jsonNode = jsonParser.readValueAsTree();
        log.trace("Deserializing {} from {} -> {}", clazz, jsonParser.currentName(), jsonNode);

        return YamlUtils.getInstance().readDynamicDeserializable(configFile, clazz, jsonNode);
    }
}
