package com.giuliolongfils.agitation.config;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

@Slf4j
public final class YamlWriter {

    private static final YamlWriter INSTANCE = new YamlWriter();

    public static YamlWriter getInstance() {
        return INSTANCE;
    }

    private final ObjectWriter writer = new YAMLMapper()
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .writerWithDefaultPrettyPrinter();

    @SneakyThrows
    public String write(Object object) {
        return writer.writeValueAsString(object);
    }
}
