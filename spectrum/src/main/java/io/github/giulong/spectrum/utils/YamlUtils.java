package io.github.giulong.spectrum.utils;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.jackson.deserializers.*;
import io.github.giulong.spectrum.internals.jackson.views.Views.Public;
import io.github.giulong.spectrum.pojos.DynamicDeserializersConfiguration;
import io.github.giulong.spectrum.utils.environments.Environment;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

@Slf4j
@Getter
public final class YamlUtils {

    private static final YamlUtils INSTANCE = new YamlUtils();
    private static final Path RESOURCES = Path.of("src", "test", "resources");
    private static final List<String> EXTENSIONS = List.of(".yaml", ".yml");

    private final ClassLoader classLoader = YamlUtils.class.getClassLoader();

    private final ObjectMapper yamlMapper = YAMLMapper
            .builder()
            .defaultMergeable(true)
            .addModules(
                    new JavaTimeModule(),
                    buildModuleFor(Object.class, InterpolatedObjectDeserializer.getInstance()),
                    buildModuleFor(String.class, InterpolatedStringDeserializer.getInstance()),
                    buildModuleFor(boolean.class, InterpolatedBooleanDeserializer.getInstance()),
                    buildModuleFor(java.util.logging.Level.class, UtilLogLevelDeserializer.getInstance()),
                    buildModuleFor(Level.class, LogbackLogLevelDeserializer.getInstance()),
                    buildModuleFor(Duration.class, DurationDeserializer.getInstance()),
                    buildModuleFor(Driver.class, DriverDeserializer.getInstance()),
                    buildModuleFor(Environment.class, EnvironmentDeserializer.getInstance()),
                    buildModuleFor(Class.class, ClassDeserializer.getInstance()))
            .build();

    private final ObjectMapper dynamicConfYamlMapper = YAMLMapper
            .builder()
            .defaultMergeable(true)
            .addModules(
                    new JavaTimeModule(),
                    buildModuleFor(Object.class, InterpolatedObjectDeserializer.getInstance()),
                    buildModuleFor(String.class, InterpolatedStringDeserializer.getInstance()),
                    buildModuleFor(boolean.class, InterpolatedBooleanDeserializer.getInstance()),
                    buildModuleFor(java.util.logging.Level.class, UtilLogLevelDeserializer.getInstance()),
                    buildModuleFor(Level.class, LogbackLogLevelDeserializer.getInstance()),
                    buildModuleFor(Duration.class, DurationDeserializer.getInstance()))
            .build();

    private final ObjectWriter writer = YAMLMapper
            .builder()
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .addModules(new JavaTimeModule())
            .build()
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

    public List<Path> findValidPathsFor(final String file) {
        return Stream.concat(Stream.of(file), EXTENSIONS
                        .stream()
                        .map(e -> String.format("%s%s", file, e)))
                .map(RESOURCES::resolve)
                .toList();
    }

    public String findTheFirstValidFileFrom(List<Path> paths) {
        return paths
                .stream()
                .peek(f -> log.debug("Looking for file {}", f))
                .filter(Files::exists)
                .peek(f -> log.debug("Found file {}", f))
                .findFirst()
                .orElseThrow()
                .getFileName()
                .toString();
    }

    public String findFile(final String file, final boolean internal) {
        if (internal) {
            return file;
        }

        final List<Path> paths = findValidPathsFor(file);

        if (paths
                .stream()
                .peek(f -> log.debug("Checking if file {} exists", f))
                .noneMatch(Files::exists)) {
            log.warn("File {} not found.", file);
            return null;
        }

        final String fileWithExtension = findTheFirstValidFileFrom(paths);
        final Path directory = Path.of(file).getParent();

        return directory != null
                ? directory.resolve(fileWithExtension).toString()
                : fileWithExtension;
    }

    public <T> T read(final String file, final Class<T> clazz, final boolean internal) {
        final String fileFound = findFile(file, internal);
        if (fileFound == null) {
            return null;
        }

        log.debug("Reading {} file '{}' onto an instance of {}", internal ? "internal" : "client", fileFound, clazz.getSimpleName());
        return read(yamlMapper, fileFound, clazz);
    }

    public <T> T read(final String file, final Class<T> clazz) {
        return read(file, clazz, false);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(file, clazz, true);
    }

    @SneakyThrows
    public <T> T readNode(final String node, final String file, final Class<T> clazz, final boolean internal) {
        final String fileFound = findFile(file, internal);
        if (fileFound == null) {
            return null;
        }

        log.debug("Reading node '{}' of {} file '{}' onto an instance of {}", node, internal ? "internal" : "client", fileFound, clazz.getSimpleName());
        final JsonNode root = yamlMapper.readTree(classLoader.getResource(fileFound));
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
        final String fileFound = findFile(file, false);
        if (fileFound == null) {
            log.warn("File {} not found. Skipping update of the instance of {}", file, t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), fileFound);
        yamlMapper
                .readerForUpdating(t)
                .withView(Public.class)
                .readValue(classLoader.getResource(fileFound));
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
