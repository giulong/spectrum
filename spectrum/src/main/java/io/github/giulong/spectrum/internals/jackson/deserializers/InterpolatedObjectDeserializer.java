package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.github.giulong.spectrum.utils.Vars;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class InterpolatedObjectDeserializer extends JsonDeserializer<Object> {

    private static final InterpolatedObjectDeserializer INSTANCE = new InterpolatedObjectDeserializer();
    private static final Pattern INT_PATTERN = Pattern.compile("(?<placeholder>\\$<(?<varName>[\\w.]+)(:-(?<defaultValue>[\\w~.:/\\\\]*))?>)");
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(.\\d+|,\\d+)?");

    private final Vars vars = Vars.getInstance();
    private final ObjectMapper objectMapper = YAMLMapper.builder().build();

    public static InterpolatedObjectDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Object deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final JsonNode jsonNode = jsonParser.readValueAsTree();
        final String currentName = jsonParser.currentName();
        final JsonNodeType jsonNodeType = jsonNode.getNodeType();

        log.trace("Deserializing {} from {} -> {}", jsonNodeType, currentName, jsonNode);

        return switch (jsonNodeType) {
            case NUMBER -> jsonNode.numberValue();
            case STRING -> traverse(jsonNode.textValue(), currentName);
            case OBJECT -> traverse(objectMapper.convertValue(jsonNode, Map.class), currentName);
            case ARRAY -> traverse(objectMapper.convertValue(jsonNode, List.class), currentName);
            default -> traverse(jsonNode, currentName);
        };
    }

    Object traverse(final Object value, final String currentName) {
        return switch (value) {
            case String v -> {
                final Matcher matcher = INT_PATTERN.matcher(v);
                yield matcher.matches()
                        ? interpolate(v, currentName, matcher)
                        : InterpolatedStringDeserializer.getInstance().interpolate(v, currentName);
            }
            case Map<?, ?> m -> m
                    .entrySet()
                    .stream()
                    .collect(toMap(Map.Entry::getKey, e -> traverse(e.getValue(), currentName)));
            case List<?> l -> l.stream().map(e -> traverse(e, currentName)).toList();
            default -> value;
        };
    }

    int interpolate(final String value, final String currentName, final Matcher matcher) {
        final String varName = matcher.group("varName");
        final String placeholder = matcher.group("placeholder");
        final String defaultValue = matcher.group("defaultValue");
        final String envVar = System.getenv(varName);
        final String envVarOrPlaceholder = envVar != null ? envVar : placeholder;
        final String systemProperty = System.getProperty(varName, envVarOrPlaceholder);

        String interpolatedValue = String.valueOf(vars.getOrDefault(varName, systemProperty));

        if (value.equals(interpolatedValue)) {
            if (defaultValue == null) {
                log.debug("No variable found to interpolate '{}' for key '{}'", value, currentName);
            } else {
                log.trace("No variable found to interpolate '{}' for key '{}'. Using provided default '{}'", value, currentName, defaultValue);
                interpolatedValue = value.replace(placeholder, defaultValue);
            }
        } else {
            log.trace("Interpolated value for key '{}: {}' -> '{}'", currentName, value, interpolatedValue);
        }

        return isNumber(interpolatedValue) ? Integer.parseInt(interpolatedValue) : 0;
    }

    boolean isNumber(final String value) {
        return NUMBER.matcher(value).matches();
    }
}
