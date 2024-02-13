package io.github.giulong.spectrum.utils;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.jackson.deserializers.*;
import io.github.giulong.spectrum.internals.jackson.views.Views.Public;
import io.github.giulong.spectrum.pojos.DynamicDeserializersConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

@Slf4j
@Getter
public final class YamlUtils {

    private static final YamlUtils INSTANCE = new YamlUtils();
    private static final Path RESOURCES = Path.of("src", "test", "resources");

    private final ClassLoader classLoader = YamlUtils.class.getClassLoader();
    private final ObjectMapper propertiesMapper = new JavaPropsMapper();

    private final ObjectMapper yamlMapper = new YAMLMapper()
            .setDefaultMergeable(true)
            .registerModules(
                    new JavaTimeModule(),
                    buildModuleFor(Object.class, InterpolatedObjectDeserializer.getInstance()),
                    buildModuleFor(String.class, InterpolatedStringDeserializer.getInstance()),
                    buildModuleFor(boolean.class, InterpolatedBooleanDeserializer.getInstance()),
                    buildModuleFor(java.util.logging.Level.class, UtilLogLevelDeserializer.getInstance()),
                    buildModuleFor(Level.class, LogbackLogLevelDeserializer.getInstance()),
                    buildModuleFor(Duration.class, DurationDeserializer.getInstance()),
                    buildModuleFor(Driver.class, DriverDeserializer.getInstance()),
                    buildModuleFor(Class.class, ClassDeserializer.getInstance())
            );

    private final ObjectMapper dynamicConfYamlMapper = new YAMLMapper()
            .setDefaultMergeable(true)
            .registerModules(
                    new JavaTimeModule(),
                    buildModuleFor(Object.class, InterpolatedObjectDeserializer.getInstance()),
                    buildModuleFor(String.class, InterpolatedStringDeserializer.getInstance()),
                    buildModuleFor(boolean.class, InterpolatedBooleanDeserializer.getInstance()),
                    buildModuleFor(java.util.logging.Level.class, UtilLogLevelDeserializer.getInstance()),
                    buildModuleFor(Level.class, LogbackLogLevelDeserializer.getInstance()),
                    buildModuleFor(Duration.class, DurationDeserializer.getInstance())
            );

    private final ObjectWriter writer = new YAMLMapper()
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .registerModules(new JavaTimeModule())
            .writerWithDefaultPrettyPrinter();

    @SuppressWarnings("unchecked")
    private YamlUtils() {
        readInternal("yaml/dynamicDeserializersConfiguration.yaml", DynamicDeserializersConfiguration.class)
                .getDynamicDeserializers()
                .stream()
                .map(DynamicDeserializer.class::cast)
                .peek(deserializer -> log.trace("Registering dynamic deserializer module {}", deserializer.getClazz().getSimpleName()))
                .forEach(deserializer -> {
                    final Class<?> clazz = deserializer.getClazz();
                    yamlMapper.registerModule(new SimpleModule(clazz.getSimpleName()).addDeserializer(clazz, deserializer));
                });
    }

    public static YamlUtils getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public SimpleModule buildModuleFor(final Class<?> clazz, final JsonDeserializer jsonDeserializer) {
        return new SimpleModule(jsonDeserializer.getClass().getSimpleName()).addDeserializer(clazz, jsonDeserializer);
    }

    public boolean notExists(final String file, final boolean internal) {
        final Path path = Path.of(file);
        if (!internal && Files.notExists(RESOURCES.resolve(path))) {
            log.warn("File {} not found.", path);
            return true;
        }

        return false;
    }

    public <T> T read(final String file, final Class<T> clazz, final boolean internal) {
        if (notExists(file, internal)) {
            return null;
        }

        log.debug("Reading {} file '{}' onto an instance of {}", internal ? "internal" : "client", file, clazz.getSimpleName());
        return read(yamlMapper, file, clazz);
    }

    public <T> T read(final String file, final Class<T> clazz) {
        return read(file, clazz, false);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(file, clazz, true);
    }

    public <T> T readProperties(final String file, final Class<T> clazz) {
        log.debug("Reading properties file '{}' onto an instance of {}", file, clazz.getSimpleName());
        return read(propertiesMapper, file, clazz);
    }

    @SneakyThrows
    public <T> T readNode(final String node, final String file, final Class<T> clazz, final boolean internal) {
        if (notExists(file, internal)) {
            return null;
        }

        log.debug("Reading node '{}' of {} file '{}' onto an instance of {}", node, internal ? "internal" : "client", file, clazz.getSimpleName());
        final JsonNode root = yamlMapper.readTree(classLoader.getResource(file));
        return yamlMapper.convertValue(root.at(node), clazz);
    }

    public <T> T readNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, file, clazz, false);
    }

    public <T> T readInternalNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, file, clazz, true);
    }

    @SneakyThrows
    public <T> T readDynamicDeserializable(final String configFile, final Class<T> clazz, final JsonNode jsonNode) {
        log.debug("Reading dynamic conf file '{}' onto an instance of {}", configFile, clazz.getSimpleName());
        final T t = read(dynamicConfYamlMapper, configFile, clazz);
        return dynamicConfYamlMapper
                .readerForUpdating(t)
                .readValue(jsonNode);
    }

    @SneakyThrows
    public <T> void updateWithFile(final T t, final String file) {
        final Path path = Path.of(file);
        if (Files.notExists(RESOURCES.resolve(path))) {
            log.warn("File {} not found. Skipping update of the instance of {}", path, t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), file);
        yamlMapper
                .readerForUpdating(t)
                .withView(Public.class)
                .readValue(classLoader.getResource(file));
    }

    @SneakyThrows
    public <T> void updateWithInternalFile(final T t, final String file) {
        log.debug("Updating the instance of {} with internal file '{}'", t.getClass().getSimpleName(), file);
        yamlMapper
                .readerForUpdating(t)
                .readValue(classLoader.getResource(file));
    }

    @SneakyThrows
    public <T> T read(final ObjectMapper objectMapper, final String fileName, final Class<T> clazz) {
        return objectMapper.readValue(classLoader.getResource(fileName), clazz);
    }

    @SneakyThrows
    public String write(final Object object) {
        return writer.writeValueAsString(object);
    }
}
