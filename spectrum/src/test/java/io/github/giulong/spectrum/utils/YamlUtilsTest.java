package io.github.giulong.spectrum.utils;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

import io.github.giulong.spectrum.TestYaml;
import io.github.giulong.spectrum.utils.file_providers.FileProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tools.jackson.databind.*;
import tools.jackson.dataformat.yaml.YAMLMapper;

class YamlUtilsTest {

    @Mock
    private YAMLMapper yamlMapper;

    @Mock
    private ObjectReader reader;

    @Mock
    private ObjectWriter writer;

    @Mock
    private JsonNode jsonNode;

    @Mock
    private TestYaml testYaml;

    @Mock
    private TestYaml.ObjectKey objectKey;

    @Mock
    private FileProvider fileProvider;

    @Captor
    private ArgumentCaptor<InputStream> inputStreamArgumentCaptor;

    @InjectMocks
    private YamlUtils yamlUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(YamlUtils.getInstance(), YamlUtils.getInstance());
    }

    @Test
    @DisplayName("on construction, all the dynamic deserializers should be registered on the yamlMapper, while not on the dynamicConfYamlMapper")
    void construction() {
        final Set<String> deserializersNames = ((YAMLMapper) Reflections.getFieldValue("yamlMapper", yamlUtils))
                .registeredModules()
                .stream()
                .map(JacksonModule::getRegistrationId)
                .map(Object::toString)
                .collect(toSet());

        final Set<String> dynamicDeserializersNames = ((YAMLMapper) Reflections.getFieldValue("dynamicConfYamlMapper", yamlUtils))
                .registeredModules()
                .stream()
                .map(JacksonModule::getRegistrationId)
                .map(Object::toString)
                .collect(toSet());

        assertEquals(Set.of(
                "java.lang.Object",
                "java.lang.String",
                "boolean",
                "java.util.logging.Level",
                "ch.qos.logback.classic.Level",
                "java.time.Duration",
                "io.github.giulong.spectrum.drivers.Driver",
                "io.github.giulong.spectrum.utils.environments.Environment",
                "java.lang.Class",
                "java.util.Random",
                "java.awt.Color",
                "io.github.giulong.spectrum.utils.reporters.LogReporter$LogTestBookReporter",
                "io.github.giulong.spectrum.utils.reporters.FileReporter$TxtTestBookReporter",
                "io.github.giulong.spectrum.utils.reporters.FileReporter$HtmlTestBookReporter",
                "io.github.giulong.spectrum.utils.reporters.LogReporter$LogSummaryReporter",
                "io.github.giulong.spectrum.utils.reporters.FileReporter$TxtSummaryReporter",
                "io.github.giulong.spectrum.utils.reporters.FileReporter$HtmlSummaryReporter"),
                deserializersNames);

        assertEquals(Set.of(
                "java.lang.Object",
                "java.lang.String",
                "boolean",
                "java.util.logging.Level",
                "ch.qos.logback.classic.Level",
                "java.time.Duration"),
                dynamicDeserializersNames);

        assertFalse(((ObjectWriter) Reflections.getFieldValue("writer", yamlUtils)).isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }

    @Test
    @DisplayName("read should return null if the provided client file doesn't exist")
    void readNotExistingClientFile() {
        assertNull(yamlUtils.readClient("not-existing", TestYaml.class));
    }

    @DisplayName("read should return an instance of the provided class deserializing the provided file")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void read(final String file) {
        assertEquals("value", Objects.requireNonNull(yamlUtils.readInternal(file, TestYaml.class)).getKey());
    }

    @DisplayName("overloaded read should return an instance of the provided class deserializing the provided file")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void readClient(final String file) {
        assertEquals("value", yamlUtils.read(new YAMLMapper().reader(), file, TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readInternal should return an instance of the provided class deserializing the provided file")
    void readInternal() {
        assertEquals("value", yamlUtils.readInternal("test.yaml", TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readNode should return null if the provided client file doesn't exist")
    void readNotExistingClientNode() {
        assertNull(yamlUtils.readClientNode("/objectKey", "not-existing"));
    }

    @DisplayName("readNode should check if the provided file exists and return the node requested")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void readNode(final String file) {
        final TestYaml.ObjectKey value = Objects.requireNonNull(yamlUtils.readInternalNode("/objectKey", file));
        assertEquals("objectValue", value.getObjectField());
    }

    @DisplayName("readNode for client-side files should just delegate to the readNode method")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void readClientNode(final String file) {
        final Class<TestYaml.ObjectKey> clazz = TestYaml.ObjectKey.class;
        final String node = "/objectKey";

        Reflections.setField("yamlMapper", yamlUtils, yamlMapper);

        when(fileProvider.find(file)).thenReturn(file);
        when(fileProvider.augment(yamlMapper)).thenReturn(reader);
        when(reader.readTree(any(InputStream.class))).thenReturn(jsonNode);
        when(jsonNode.at(node)).thenReturn(jsonNode);
        when(yamlMapper.convertValue(jsonNode, clazz)).thenReturn(objectKey);

        assertEquals(objectKey, yamlUtils.readNode(fileProvider, node, file, clazz));
    }

    @Test
    @DisplayName("readInternalNode should just delegate to the readNode method")
    void readInternalNode() {
        final TestYaml.ObjectKey value = yamlUtils.readInternalNode("/objectKey", "test.yaml");
        assertEquals("objectValue", value.getObjectField());
    }

    @Test
    @DisplayName("readDynamicDeserializable should read the internal dynamic deserializable configuration provided, and merge the jsonNode provided on the created instance")
    void readDynamicDeserializable() {
        final Class<TestYaml> clazz = TestYaml.class;
        Reflections.setField("dynamicConfYamlMapper", yamlUtils, yamlMapper);

        when(yamlMapper.reader()).thenReturn(reader);
        when(reader.forType(clazz)).thenReturn(reader);
        when(reader.readValue(any(InputStream.class))).thenReturn(testYaml);
        when(reader.withValueToUpdate(testYaml)).thenReturn(reader);
        when(reader.readValue(jsonNode)).thenReturn(testYaml);

        assertEquals(testYaml, yamlUtils.readDynamicDeserializable("test.yaml", clazz, jsonNode));
    }

    @Test
    @DisplayName("updateNode should update the provided object with the content of the node at the provided file")
    void updateNode() {
        final String file = "test.yaml";
        final String node = "node";

        Reflections.setField("yamlMapper", yamlUtils, yamlMapper);
        Reflections.setField("clientFileProvider", yamlUtils, fileProvider);

        when(fileProvider.find(file)).thenReturn(file);
        when(fileProvider.augment(yamlMapper)).thenReturn(reader);
        when(reader.withValueToUpdate(testYaml)).thenReturn(reader);
        when(yamlMapper.readTree(inputStreamArgumentCaptor.capture())).thenReturn(jsonNode);
        when(jsonNode.at(node)).thenReturn(jsonNode);
        when(reader.readValue(jsonNode)).thenReturn(testYaml);

        yamlUtils.updateNode(testYaml, node, file, fileProvider);
    }

    @DisplayName("updateWithFile should update the provided instance with the file provided, reading only public fields")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void updateWithFile(final String file) {
        Reflections.setField("yamlMapper", yamlUtils, yamlMapper);
        Reflections.setField("clientFileProvider", yamlUtils, fileProvider);

        when(fileProvider.find(file)).thenReturn(file);
        when(fileProvider.augment(yamlMapper)).thenReturn(reader);
        when(reader.forType(TestYaml.class)).thenReturn(reader);
        when(reader.withValueToUpdate(testYaml)).thenReturn(reader);
        when(reader.readValue(inputStreamArgumentCaptor.capture())).thenReturn(testYaml);

        yamlUtils.updateWithClientFile(testYaml, file);
    }

    @Test
    @DisplayName("updateWithFile should do nothing if the provided file doesn't exist")
    void updateWithNotExistingFile() {
        final String file = "file";

        when(fileProvider.find(file)).thenReturn(null);

        yamlUtils.updateWithFile(testYaml, file, fileProvider);

        verifyNoInteractions(testYaml);
        verifyNoMoreInteractions(fileProvider);
    }

    @Test
    @DisplayName("updateWithInternalFile should update the provided instance with the internal file provided")
    void updateWithInternalFile() {
        final String file = "test.yaml";

        Reflections.setField("yamlMapper", yamlUtils, yamlMapper);
        Reflections.setField("internalFileProvider", yamlUtils, fileProvider);

        when(fileProvider.find(file)).thenReturn(file);
        when(fileProvider.augment(yamlMapper)).thenReturn(reader);
        when(reader.forType(TestYaml.class)).thenReturn(reader);
        when(reader.withValueToUpdate(testYaml)).thenReturn(reader);
        when(reader.readValue(any(InputStream.class))).thenReturn(testYaml);

        yamlUtils.updateWithInternalFile(testYaml, file);
    }

    @Test
    @DisplayName("updateWithInternalNode should update the provided instance with the node of the internal file provided")
    void updateWithInternalNode() {
        final String file = "test.yaml";
        final String node = "node";

        Reflections.setField("yamlMapper", yamlUtils, yamlMapper);
        Reflections.setField("internalFileProvider", yamlUtils, fileProvider);

        when(fileProvider.find(file)).thenReturn(file);
        when(fileProvider.augment(yamlMapper)).thenReturn(reader);
        when(reader.withValueToUpdate(testYaml)).thenReturn(reader);
        when(yamlMapper.readTree(inputStreamArgumentCaptor.capture())).thenReturn(jsonNode);
        when(jsonNode.at(node)).thenReturn(jsonNode);
        when(reader.readValue(jsonNode)).thenReturn(testYaml);

        yamlUtils.updateWithInternalNode(testYaml, node, file);
    }

    @Test
    @DisplayName("updateWithClientNode should update the provided instance with the node of the client file provided")
    void updateWithClientNode() {
        final String file = "test.yaml";
        final String node = "node";

        Reflections.setField("yamlMapper", yamlUtils, yamlMapper);
        Reflections.setField("clientFileProvider", yamlUtils, fileProvider);

        when(fileProvider.find(file)).thenReturn(file);
        when(fileProvider.augment(yamlMapper)).thenReturn(reader);
        when(reader.withValueToUpdate(testYaml)).thenReturn(reader);
        when(yamlMapper.readTree(inputStreamArgumentCaptor.capture())).thenReturn(jsonNode);
        when(jsonNode.at(node)).thenReturn(jsonNode);
        when(reader.readValue(jsonNode)).thenReturn(testYaml);

        yamlUtils.updateWithClientNode(testYaml, node, file);
    }

    @Test
    @DisplayName("write should just call the writeValueAsString of the provided object, printing internal fields as well")
    void write() {
        final String string = "string";

        Reflections.setField("writer", yamlUtils, writer);

        when(writer.writeValueAsString(testYaml)).thenReturn(string);

        assertEquals(string, yamlUtils.write(testYaml));
    }
}
