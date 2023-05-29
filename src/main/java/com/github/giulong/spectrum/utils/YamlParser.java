package com.github.giulong.spectrum.utils;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.giulong.spectrum.browsers.Browser;
import com.github.giulong.spectrum.internals.jackson.*;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class YamlParser {

    private static final YamlParser INSTANCE = new YamlParser();
    private static final Path RESOURCES = Paths.get("src", "test", "resources");

    public static YamlParser getInstance() {
        return INSTANCE;
    }

    private final ObjectMapper yamlMapper = new YAMLMapper()
            .setDefaultMergeable(true)
            .registerModules(
                    new JavaTimeModule(),
                    new SimpleModule().addDeserializer(String.class, new InterpolatedStringDeserializer()),
                    new SimpleModule().addDeserializer(boolean.class, new InterpolatedBooleanDeserializer()),
                    new SimpleModule().addDeserializer(java.util.logging.Level.class, new UtilLogLevelDeserializer()),
                    new SimpleModule().addDeserializer(Level.class, new LogbackLogLevelDeserializer()),
                    new SimpleModule().addDeserializer(Duration.class, new DurationDeserializer()),
                    new SimpleModule().addDeserializer(Browser.class, new BrowserDeserializer())
            );

    public boolean notExists(final String file, final boolean internal) {
        final Path path = Paths.get(file);
        if (!internal && Files.notExists(RESOURCES.resolve(path))) {
            log.warn("File {} not found.", path);
            return true;
        }

        return false;
    }

    @SneakyThrows
    public <T> T read(final String file, final Class<T> clazz, final boolean internal) {
        if (notExists(file, internal)) {
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
    public <T> T readNode(final String node, final String file, final Class<T> clazz, final boolean internal) {
        if (notExists(file, internal)) {
            return null;
        }

        log.debug("Reading node '{}' of internal file '{}' onto an instance of {}", node, file, clazz.getSimpleName());
        final JsonNode root = yamlMapper.readTree(YamlParser.class.getClassLoader().getResource(file));
        return yamlMapper.convertValue(root.at(node), clazz);
    }

    public <T> T readNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, file, clazz, false);
    }

    public <T> T readInternalNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, file, clazz, true);
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
