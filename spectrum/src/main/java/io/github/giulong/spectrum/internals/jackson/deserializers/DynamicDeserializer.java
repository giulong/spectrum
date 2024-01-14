package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.giulong.spectrum.utils.YamlUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Getter
@SuppressWarnings("unused")
public class DynamicDeserializer<T> extends JsonDeserializer<T> {

    private Class<T> clazz;
    private String configFile;

    @Override
    @SneakyThrows
    public T deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final JsonNode jsonNode = jsonParser.readValueAsTree();
        log.trace("Deserializing {} from {} -> {}", clazz, jsonParser.currentName(), jsonNode);

        return YamlUtils.getInstance().readDynamicDeserializable(configFile, clazz, jsonNode);
    }
}
