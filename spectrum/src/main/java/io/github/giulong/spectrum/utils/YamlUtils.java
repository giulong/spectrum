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
import io.github.giulong.spectrum.internals.jackson.views.Views;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Getter
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
        return read(clientFileProvider.find(file), clazz);
    }

    public <T> T readInternal(final String file, final Class<T> clazz) {
        return read(internalFileProvider.find(file), clazz);
    }

    public <T> T readClientNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, clientFileProvider.find(file), clazz);
    }

    public <T> T readInternalNode(final String node, final String file, final Class<T> clazz) {
        return readNode(node, internalFileProvider.find(file), clazz);
    }

    public <T> void updateWithClientFile(final T t, final String file) {
        updateWithFile(t, clientFileProvider.find(file), clientFileProvider.getViews());
    }

    public <T> void updateWithInternalFile(final T t, final String file) {
        updateWithFile(t, internalFileProvider.find(file), internalFileProvider.getViews());
    }

    @SneakyThrows
    public String write(final Object object) {
        return writer.writeValueAsString(object);
    }

    @SneakyThrows
    public <T> T readDynamicDeserializable(final String configFile, final Class<T> clazz, final JsonNode jsonNode) {
        log.debug("Reading dynamic conf file '{}' onto an instance of {}", configFile, clazz.getSimpleName());
        final T t = read(dynamicConfYamlMapper, configFile, clazz);
        return dynamicConfYamlMapper
                .readerForUpdating(t)
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
    <T> T read(final ObjectMapper objectMapper, final String fileName, final Class<T> clazz) {
        return objectMapper.readValue(classLoader.getResource(fileName), clazz);
    }

    <T> T read(final String file, final Class<T> clazz) {
        if (file == null) {
            return null;
        }

        log.debug("Reading file '{}' onto an instance of {}", file, clazz.getSimpleName());
        return read(yamlMapper, file, clazz);
    }

    @SneakyThrows
    <T> T readNode(final String node, final String file, final Class<T> clazz) {
        if (file == null) {
            return null;
        }

        log.debug("Reading node '{}' of file '{}' onto an instance of {}", node, file, clazz.getSimpleName());
        final JsonNode root = yamlMapper.readTree(classLoader.getResource(file));
        return yamlMapper.convertValue(root.at(node), clazz);
    }

    @SneakyThrows
    <T> void updateWithFile(final T t, final String file, final Class<? extends Views> views) {
        if (file == null) {
            log.warn("File not found. Skipping update of the instance of {}", t.getClass().getSimpleName());
            return;
        }

        log.debug("Updating the instance of {} with file '{}'", t.getClass().getSimpleName(), file);
        yamlMapper
                .readerForUpdating(t)
                .withView(views)
                .readValue(classLoader.getResource(file));
    }
}
