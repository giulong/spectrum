package com.giuliolongfils.spectrum.utils;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class YamlWriter {

    private static final YamlWriter INSTANCE = new YamlWriter();

    public static YamlWriter getInstance() {
        return INSTANCE;
    }

    private final ObjectWriter writer = new YAMLMapper()
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .registerModules(new JavaTimeModule())
            .writerWithDefaultPrettyPrinter();

    @SneakyThrows
    public String write(Object object) {
        return writer.writeValueAsString(object);
    }
}
