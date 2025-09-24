package io.github.giulong.spectrum.utils;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.internals.jackson.deserializers.*;
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

import java.time.Duration;
import java.util.Random;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static lombok.AccessLevel.PRIVATE;

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
                    buildModuleFor(Class.class, ClassDeserializer.getInstance()),
                    buildModuleFor(Random.class, RandomDeserializer.getInstance()),
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

    public static YamlUtils getInstance() {
        return INSTANCE;
    }

    public <T> T readClient(final String file, final Class<T> clazz) {
        return read(clientFileProvider, file, clazz);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(internalFileProvider, file, clazz);
    }

    public <T> T readClientNode(final String node, final String file, final Class<T> clazz) {
        return readNode(clientFileProvider, node, file, clazz);
    }

    public <T> T readInternalNode(final String node, final String file, final Class<T> clazz) {
        return readNode(internalFileProvider, node, file, clazz);
    }

    public <T> void updateWithClientFile(final T t, final String file) {
        updateWithFile(t, file, clientFileProvider);
    }

    public <T> void updateWithInternalFile(final T t, final String file) {
        updateWithFile(t, file, internalFileProvider);
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
    SimpleModule buildModuleFor(final Class<?> clazz, final JsonDeserializer jsonDeserializer) {
        return new SimpleModule(clazz.getSimpleName()).addDeserializer(clazz, jsonDeserializer);
    }

    SimpleModule buildDynamicModuleFor(final Class<?> clazz, final String file) {
        return buildModuleFor(clazz, new DynamicDeserializer<>(clazz, file));
    }

    @SneakyThrows
    <T> T read(final ObjectReader reader, final String file, final Class<T> clazz) {
        return reader.readValue(classLoader.getResourceAsStream(file), clazz);
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
    <T> T readNode(final FileProvider fileProvider, final String node, final String file, final Class<T> clazz) {
        final String fileFound = fileProvider.find(file);
        if (fileFound == null) {
            return null;
        }

        log.debug("Reading node '{}' of file '{}' onto an instance of {}", node, fileFound, clazz.getSimpleName());
        final JsonNode root = fileProvider
                .augment(yamlMapper)
                .readTree(classLoader.getResourceAsStream(fileFound));

        return yamlMapper.convertValue(root.at(node), clazz);
    }

    @SneakyThrows
    <T> void updateWithFile(final T t, final String file, final FileProvider fileProvider) {
        final String fileFound = fileProvider.find(file);
        if (fileFound == null) {
            log.warn("File not found. Skipping update of the instance of {}", t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), fileFound);
        fileProvider
                .augment(yamlMapper)
                .withValueToUpdate(t)
                .readValue(classLoader.getResourceAsStream(fileFound));
    }
}
