package io.github.giulong.spectrum.utils;

import static lombok.AccessLevel.PRIVATE;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION;
import static tools.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

import java.awt.*;
import java.time.Duration;
import java.util.Random;
import java.util.function.BiConsumer;

import ch.qos.logback.classic.Level;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.jackson.deserializers.*;
import io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.*;
import io.github.giulong.spectrum.utils.environments.Environment;
import io.github.giulong.spectrum.utils.file_providers.ClientFileProvider;
import io.github.giulong.spectrum.utils.file_providers.FileProvider;
import io.github.giulong.spectrum.utils.file_providers.InternalFileProvider;
import io.github.giulong.spectrum.utils.reporters.FileReporter.HtmlSummaryReporter;
import io.github.giulong.spectrum.utils.reporters.FileReporter.HtmlTestBookReporter;
import io.github.giulong.spectrum.utils.reporters.FileReporter.TxtSummaryReporter;
import io.github.giulong.spectrum.utils.reporters.FileReporter.TxtTestBookReporter;
import io.github.giulong.spectrum.utils.reporters.LogReporter.LogSummaryReporter;
import io.github.giulong.spectrum.utils.reporters.LogReporter.LogTestBookReporter;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.databind.*;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class YamlUtils {

    private static final YamlUtils INSTANCE = new YamlUtils();

    private final ClassLoader classLoader = YamlUtils.class.getClassLoader();
    private final FileProvider internalFileProvider = InternalFileProvider.builder().build();
    private final FileProvider clientFileProvider = ClientFileProvider.builder().build();

    private final ObjectMapper yamlMapper = YAMLMapper
            .builder()
            .defaultMergeable(true)
            .enable(PROPAGATE_TRANSIENT_MARKER, DEFAULT_VIEW_INCLUSION, ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModules(
                    buildModuleFor(Object.class, InterpolatedObjectDeserializer.getInstance()),
                    buildModuleFor(String.class, InterpolatedStringDeserializer.getInstance()),
                    buildModuleFor(boolean.class, InterpolatedBooleanDeserializer.getInstance()),
                    buildModuleFor(java.util.logging.Level.class, UtilLogLevelDeserializer.getInstance()),
                    buildModuleFor(Level.class, LogbackLogLevelDeserializer.getInstance()),
                    buildModuleFor(Duration.class, DurationDeserializer.getInstance()),
                    buildModuleFor(Driver.class, DriverDeserializer.getInstance()),
                    buildModuleFor(Environment.class, EnvironmentDeserializer.getInstance()),
                    buildModuleFor(Class.class, ClassDeserializer.getInstance()),
                    buildModuleFor(Random.class, RandomDeserializer.getInstance()),
                    buildModuleFor(Color.class, ColorDeserializer.getInstance()),
                    buildDynamicModuleFor(LogTestBookReporter.class, "yaml/dynamic/testbook/logReporter.yaml"),
                    buildDynamicModuleFor(TxtTestBookReporter.class, "yaml/dynamic/testbook/txtReporter.yaml"),
                    buildDynamicModuleFor(HtmlTestBookReporter.class, "yaml/dynamic/testbook/htmlReporter.yaml"),
                    buildDynamicModuleFor(LogSummaryReporter.class, "yaml/dynamic/summary/logReporter.yaml"),
                    buildDynamicModuleFor(TxtSummaryReporter.class, "yaml/dynamic/summary/txtReporter.yaml"),
                    buildDynamicModuleFor(HtmlSummaryReporter.class, "yaml/dynamic/summary/htmlReporter.yaml"))
            .build();

    private final ObjectMapper dynamicConfYamlMapper = YAMLMapper
            .builder()
            .defaultMergeable(true)
            .addModules(
                    buildModuleFor(Object.class, InterpolatedObjectDeserializer.getInstance()),
                    buildModuleFor(String.class, InterpolatedStringDeserializer.getInstance()),
                    buildModuleFor(boolean.class, InterpolatedBooleanDeserializer.getInstance()),
                    buildModuleFor(java.util.logging.Level.class, UtilLogLevelDeserializer.getInstance()),
                    buildModuleFor(Level.class, LogbackLogLevelDeserializer.getInstance()),
                    buildModuleFor(Duration.class, DurationDeserializer.getInstance()))
            .build();

    private final ObjectWriter writer = YAMLMapper
            .builder()
            .disable(FAIL_ON_EMPTY_BEANS)
            .build()
            .writerWithDefaultPrettyPrinter();

    public static YamlUtils getInstance() {
        return INSTANCE;
    }

    public <T> T readClient(final String file, final Class<T> clazz) {
        return read(clientFileProvider, file, clazz);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(internalFileProvider, file, clazz);
    }

    @SafeVarargs
    public final <T> T readClientNode(final String node, final String file, final T... reified) {
        return readNode(clientFileProvider, node, file, Reflections.getClassOf(reified));
    }

    @SafeVarargs
    public final <T> T readInternalNode(final String node, final String file, final T... reified) {
        return readNode(internalFileProvider, node, file, Reflections.getClassOf(reified));
    }

    public <T> void updateWithClientFile(final T t, final String file) {
        updateWithFile(t, file, clientFileProvider);
    }

    public <T> void updateWithInternalFile(final T t, final String file) {
        updateWithFile(t, file, internalFileProvider);
    }

    public <T> void updateWithInternalNode(final T t, final String node, final String file) {
        updateNode(t, node, file, internalFileProvider);
    }

    public <T> void updateWithClientNode(final T t, final String node, final String file) {
        updateNode(t, node, file, clientFileProvider);
    }

    @SneakyThrows
    public String write(final Object object) {
        return writer.writeValueAsString(object);
    }

    @SneakyThrows
    public <T> T readDynamicDeserializable(final String configFile, final Class<T> clazz, final JsonNode jsonNode) {
        log.debug("Reading dynamic conf file '{}' onto an instance of {}", configFile, clazz.getSimpleName());
        final ObjectReader reader = dynamicConfYamlMapper.reader();
        final T t = read(reader, configFile, clazz);

        return reader
                .withValueToUpdate(t)
                .readValue(jsonNode);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    SimpleModule buildModuleFor(final Class<?> clazz, final ValueDeserializer valueDeserializer) {
        return new SimpleModule(clazz.getName()).addDeserializer(clazz, valueDeserializer);
    }

    SimpleModule buildDynamicModuleFor(final Class<?> clazz, final String file) {
        return buildModuleFor(clazz, new DynamicDeserializer<>(clazz, file));
    }

    @SneakyThrows
    <T> T read(final ObjectReader reader, final String file, final String node) {
        return reader.readValue(yamlMapper.readTree(classLoader.getResourceAsStream(file)).at(node));
    }

    @SneakyThrows
    <T> T read(final ObjectReader reader, final String file, final Class<T> clazz) {
        return reader.forType(clazz).readValue(classLoader.getResourceAsStream(file));
    }

    <T> T read(final FileProvider fileProvider, final String file, final Class<T> clazz) {
        final String fileFound = fileProvider.find(file);
        if (fileFound == null) {
            return null;
        }

        log.debug("Reading file '{}' onto an instance of {}", fileFound, clazz.getSimpleName());
        return read(fileProvider.augment(yamlMapper), fileFound, clazz);
    }

    @SneakyThrows
    <T> T readNode(final FileProvider fileProvider, final String name, final String file, final Class<T> clazz) {
        final String fileFound = fileProvider.find(file);
        if (fileFound == null) {
            return null;
        }

        log.debug("Reading node '{}' of file '{}' onto an instance of {}", name, fileFound, clazz.getSimpleName());
        final JsonNode node = fileProvider
                .augment(yamlMapper)
                .readTree(classLoader.getResourceAsStream(fileFound))
                .at(name);

        return yamlMapper.convertValue(node, clazz);
    }

    <T> void updateNode(final T t, final String name, final String file, final FileProvider fileProvider) {
        updateAndAccept(t, file, fileProvider, (fileFound, reader) -> read(reader, fileFound, name));
    }

    <T> void updateWithFile(final T t, final String file, final FileProvider fileProvider) {
        updateAndAccept(t, file, fileProvider, (fileFound, reader) -> read(reader, fileFound, t.getClass()));
    }

    <T> void updateAndAccept(final T t, final String file, final FileProvider fileProvider, final BiConsumer<String, ObjectReader> callback) {
        final String fileFound = fileProvider.find(file);
        if (fileFound == null) {
            log.debug("File not found. Skipping update of the instance of {}", t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), fileFound);
        callback.accept(fileFound, fileProvider.augment(yamlMapper).withValueToUpdate(t));
    }
}
