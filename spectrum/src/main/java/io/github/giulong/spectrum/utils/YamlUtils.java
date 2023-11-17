package io.github.giulong.spectrum.utils;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.internals.jackson.deserializers.*;
import io.github.giulong.spectrum.internals.jackson.views.Views;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class YamlUtils {

    private static final YamlUtils INSTANCE = new YamlUtils();
    private static final Path RESOURCES = Path.of("src", "test", "resources");

    public static YamlUtils getInstance() {
        return INSTANCE;
    }

    private final ObjectMapper propertiesMapper = new JavaPropsMapper();

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

    private final ObjectWriter writer = new YAMLMapper()
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .registerModules(new JavaTimeModule())
            .writerWithDefaultPrettyPrinter();

    public boolean notExists(final String file, final boolean internal) {
        final Path path = Path.of(file);
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
        return yamlMapper.readValue(YamlUtils.class.getClassLoader().getResource(file), clazz);
    }

    public <T> T read(final String file, final Class<T> clazz) {
        return read(file, clazz, false);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(file, clazz, true);
    }

    @SneakyThrows
    public <T> T readProperties(final String file, final Class<T> clazz) {
        log.debug("Reading properties file '{}' onto an instance of {}", file, clazz.getSimpleName());
        return propertiesMapper.readValue(YamlUtils.class.getClassLoader().getResource(file), clazz);
    }

    @SneakyThrows
    public <T> T readNode(final String node, final String file, final Class<T> clazz, final boolean internal) {
        if (notExists(file, internal)) {
            return null;
        }

        log.debug("Reading node '{}' of {} file '{}' onto an instance of {}", node, internal ? "internal" : "client", file, clazz.getSimpleName());
        final JsonNode root = yamlMapper.readTree(YamlUtils.class.getClassLoader().getResource(file));
        return yamlMapper.convertValue(root.at(node), clazz);
    }

    public <T> T readNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, file, clazz, false);
    }

    public <T> T readInternalNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, file, clazz, true);
    }

    @SneakyThrows
    public <T> void updateWithFile(final T t, final String file) {
        final Path path = Path.of(file);
        if (Files.notExists(RESOURCES.resolve(path))) {
            log.warn("File {} not found. Skipping update of the instance of {}", path, t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), file);
        yamlMapper.readerForUpdating(t).withView(Views.Public.class).readValue(YamlUtils.class.getClassLoader().getResource(file));
    }

    @SneakyThrows
    public <T> void updateWithInternalFile(final T t, final String file) {
        log.debug("Updating the instance of {} with internal file '{}'", t.getClass().getSimpleName(), file);
        yamlMapper.readerForUpdating(t).readValue(YamlUtils.class.getClassLoader().getResource(file));
    }

    @SneakyThrows
    public String write(Object object) {
        return writer.writeValueAsString(object);
    }
}
