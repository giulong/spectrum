package io.github.giulong.spectrum.internals.jackson.deserializers;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ClassDeserializer extends ValueDeserializer<Class<?>> {

    private static final ClassDeserializer INSTANCE = new ClassDeserializer();

    public static ClassDeserializer getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    @Override
    public Class<?> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing class literal from value {}", value);

        return ClassDeserializer.class.getClassLoader().loadClass(value);
    }
}
