package com.giuliolongfils.agitation.config;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.giuliolongfils.agitation.browsers.Browser;
import com.giuliolongfils.agitation.internal.jackson.BrowserDeserializer;
import com.giuliolongfils.agitation.internal.jackson.LogbackLogLevelDeserializer;
import com.giuliolongfils.agitation.internal.jackson.UtilLogLevelDeserializer;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Builder
public class YamlParser {

    private static final Path RESOURCES = Paths.get("src", "test", "resources");
    private static final String EMPTY_YAML = "{}";

    private final ObjectMapper yamlMapper = new YAMLMapper();

    public YamlParser() {
        yamlMapper
                .setDefaultMergeable(true)
                .registerModules(
                        new SimpleModule().addDeserializer(java.util.logging.Level.class, new UtilLogLevelDeserializer()),
                        new SimpleModule().addDeserializer(Level.class, new LogbackLogLevelDeserializer()),
                        new SimpleModule().addDeserializer(Browser.class, new BrowserDeserializer())
                );
    }

    @SneakyThrows
    protected <T> T read(final String file, final Class<T> clazz, boolean internal) {
        final Path path = Paths.get(file);
        if (!internal && Files.notExists(RESOURCES.resolve(path))) {
            log.warn("File {} not found.", path);
            return null;
        }

        log.debug("Reading {} file '{}' onto an instance of {}", internal ? "internal" : "client", file, clazz.getSimpleName());
        return yamlMapper.readValue(YamlParser.class.getClassLoader().getResource(file), clazz);
    }

    public <T> T read(final String file, final Class<T> clazz) {
        return read(file, clazz, false);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(file, clazz, true);
    }

    @SneakyThrows
    public <T> void update(final T t, final String content) {
        log.debug("Updating the instance of {}", t.getClass().getSimpleName());
        log.trace("Content:\n{}", content);
        yamlMapper.readerForUpdating(t).readValue(content);
    }

    @SneakyThrows
    public <T> void updateWithFile(final T t, final String file) {
        final Path path = Paths.get(file);
        if (Files.notExists(RESOURCES.resolve(path))) {
            log.warn("File {} not found. Skipping update of the instance of {}", path, t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), file);
        yamlMapper.readerForUpdating(t).readValue(YamlParser.class.getClassLoader().getResource(file));
    }
}
