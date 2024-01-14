package io.github.giulong.spectrum.internals.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ClassDeserializer extends JsonDeserializer<Class<?>> {

    private static final ClassDeserializer INSTANCE = new ClassDeserializer();

    public static ClassDeserializer getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    @Override
    public Class<?> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final String value = jsonParser.getValueAsString();
        log.trace("Deserializing class literal from value {}", value);

        return ClassDeserializer.class.getClassLoader().loadClass(value);
    }
}
