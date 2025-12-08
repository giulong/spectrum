package io.github.giulong.spectrum.utils;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = PRIVATE)
public final class JsonUtils {

    private static final JsonUtils INSTANCE = new JsonUtils();

    private final ObjectMapper jsonMapper = JsonMapper
            .builder()
            .build()
            .setDefaultMergeable(true);

    private final ObjectWriter writer = jsonMapper
            .writerWithDefaultPrettyPrinter();

    public static JsonUtils getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    public <T> T readOrEmpty(final File file, final Class<T> clazz) {
        return file.exists()
                ? jsonMapper.readValue(file, clazz)
                : jsonMapper.readValue("{}", clazz);
    }

    @SneakyThrows
    public String write(final Object object) {
        return writer.writeValueAsString(object);
    }
}
