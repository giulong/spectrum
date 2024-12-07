package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.github.giulong.spectrum.TestYaml;
import io.github.giulong.spectrum.internals.jackson.views.Views;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YamlUtilsTest {

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
        assertEquals(Set.of(
                "jackson-datatype-jsr310",
                "Object",
                "String",
                "boolean",
                "Level",
                "Duration",
                "Driver",
                "Environment",
                "Class",
                "LogTestBookReporter",
                "TxtTestBookReporter",
                "HtmlTestBookReporter",
                "LogSummaryReporter",
                "TxtSummaryReporter",
                "HtmlSummaryReporter"
        ), yamlUtils.getYamlMapper().getRegisteredModuleIds());

        assertEquals(Set.of(
                "jackson-datatype-jsr310",
                "Object",
                "String",
                "boolean",
                "Level",
                "Duration"
        ), yamlUtils.getDynamicConfYamlMapper().getRegisteredModuleIds());

        assertFalse(yamlUtils.getWriter().isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
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
        assertEquals("value", yamlUtils.read(file, TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readInternal should return an instance of the provided class deserializing the provided file")
    void readInternal() {
        assertEquals("value", yamlUtils.readInternal("test.yaml", TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readNode should return null if the provided client file doesn't exist")
    void readNotExistingClientNode() {
        assertNull(yamlUtils.readClientNode("/objectKey", "not-existing", TestYaml.ObjectKey.class));
    }

    @DisplayName("readNode should check if the provided file exists and return the node requested")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void readNode(final String file) {
        assertEquals("objectValue", Objects.requireNonNull(yamlUtils.readInternalNode("/objectKey", file, TestYaml.ObjectKey.class)).getObjectField());
    }

    @DisplayName("readNode for client-side files should just delegate to the readNode method")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void readClientNode(final String file) {
        assertEquals("objectValue", yamlUtils.readNode("/objectKey", file, TestYaml.ObjectKey.class).getObjectField());
    }

    @Test
    @DisplayName("readInternalNode should just delegate to the readNode method")
    void readInternalNode() {
        assertEquals("objectValue", yamlUtils.readInternalNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class).getObjectField());
    }

    @Test
    @DisplayName("readDynamicDeserializable should read the internal dynamic deserializable configuration provided, and merge the jsonNode provided on the created instance")
    void readDynamicDeserializable() {
        final JsonNode jsonNode = JsonNodeFactory.instance
                .objectNode()
                .put("key", "merged");

        final TestYaml mergedYaml = yamlUtils.readDynamicDeserializable("test.yaml", TestYaml.class, jsonNode);
        assertEquals("merged", mergedYaml.getKey());
        assertEquals("objectValue", mergedYaml.getObjectKey().getObjectField());
    }

    @DisplayName("updateWithFile should update the provided instance with the file provided, reading only public fields")
    @ParameterizedTest(name = "with file {0}")
    @ValueSource(strings = {"test.yaml", "test.yml", "configurations/test.yaml"})
    void updateWithFile(final String file) {
        final TestYaml testYaml = TestYaml.builder()
                .key("original")
                .internalKey(TestYaml.InternalKey.builder().field("field").build())
                .build();

        yamlUtils.updateWithClientFile(testYaml, file);
        assertEquals("value", testYaml.getKey());
        assertEquals("field", testYaml.getInternalKey().getField()); // from the original pojo above: it's not updated with the content of test.yaml
    }

    @Test
    @DisplayName("updateWithFile should do nothing if the provided file doesn't exist")
    void updateWithNotExistingFile() {
        final TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlUtils.updateWithFile(testYaml, null, Views.Public.class);
        assertEquals("original", testYaml.getKey());
    }

    @Test
    @DisplayName("updateWithInternalFile should update the provided instance with the internal file provided")
    void updateWithInternalFile() {
        final TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlUtils.updateWithInternalFile(testYaml, "test.yaml");
        assertEquals("value", testYaml.getKey());
    }

    @Test
    @DisplayName("write should just call the writeValueAsString of the provided object, printing internal fields as well")
    void write() {
        final TestYaml testYaml = mock(TestYaml.class);
        final TestYaml.ObjectKey objectKey = mock(TestYaml.ObjectKey.class);
        final TestYaml.InternalKey internalKey = mock(TestYaml.InternalKey.class);

        when(testYaml.getKey()).thenReturn("value");
        when(testYaml.getObjectKey()).thenReturn(objectKey);
        when(testYaml.getInternalKey()).thenReturn(internalKey);
        when(objectKey.getObjectField()).thenReturn("field");
        when(internalKey.getField()).thenReturn("internalField");
        assertEquals("---\nkey: \"value\"\nobjectKey:\n  objectField: \"field\"\ninternalKey:\n  field: \"internalField\"\n", yamlUtils.write(testYaml));
    }
}
