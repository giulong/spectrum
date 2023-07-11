package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.TestYaml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("YamlUtils")
class YamlUtilsTest {

    @InjectMocks
    private YamlUtils yamlUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        assertSame(YamlUtils.getInstance(), YamlUtils.getInstance());
    }

    @Test
    @DisplayName("read should return null if the provided client file doesn't exist")
    public void readNotExistingClientFile() {
        assertNull(yamlUtils.read("not-existing", TestYaml.class, false));
    }

    @Test
    @DisplayName("read should return an instance of the provided class deserializing the provided file")
    public void read() {
        assertEquals("value", Objects.requireNonNull(yamlUtils.read("test.yaml", TestYaml.class, true)).getKey());
    }

    @Test
    @DisplayName("overloaded read should return an instance of the provided class deserializing the provided file")
    public void readClient() {
        assertEquals("value", yamlUtils.read("test.yaml", TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readInternal should return an instance of the provided class deserializing the provided file")
    public void readInternal() {
        assertEquals("value", yamlUtils.readInternal("test.yaml", TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readNode should return null if the provided client file doesn't exist")
    public void readNotExistingClientNode() {
        assertNull(yamlUtils.readNode("/objectKey", "not-existing", TestYaml.ObjectKey.class, false));
    }

    @Test
    @DisplayName("readNode should check if the provided file exists and return the node requested")
    public void readNode() {
        assertEquals("objectValue", Objects.requireNonNull(yamlUtils.readNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class, true)).getObjectField());
    }

    @Test
    @DisplayName("readNode for client-side files should just delegate to the readNode method")
    public void readClientNode() {
        assertEquals("objectValue", yamlUtils.readNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class).getObjectField());
    }

    @Test
    @DisplayName("readInternalNode should just delegate to the readNode method")
    public void readInternalNode() {
        assertEquals("objectValue", yamlUtils.readInternalNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class).getObjectField());
    }

    @Test
    @DisplayName("update should update the provided instance with the string content provided")
    public void update() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlUtils.update(testYaml, "{ \"key\": \"value\" }");
        assertEquals("value", testYaml.getKey());
    }

    @Test
    @DisplayName("updateWithFile should update the provided instance with the file provided")
    public void updateWithFile() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlUtils.updateWithFile(testYaml, "test.yaml");
        assertEquals("value", testYaml.getKey());
    }

    @Test
    @DisplayName("updateWithFile should do nothing if the provided file doesn't exist")
    public void updateWithNotExistingFile() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlUtils.updateWithFile(testYaml, "not-existing");
        assertEquals("original", testYaml.getKey());
    }

    @Test
    @DisplayName("updateWithInternalFile should update the provided instance with the internal file provided")
    public void updateWithInternalFile() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlUtils.updateWithInternalFile(testYaml, "test.yaml");
        assertEquals("value", testYaml.getKey());
    }

    @Test
    @DisplayName("write should just call the writeValueAsString of the provided object")
    public void write() {
        final TestYaml testYaml = mock(TestYaml.class);
        final TestYaml.ObjectKey objectKey = mock(TestYaml.ObjectKey.class);

        when(testYaml.getKey()).thenReturn("value");
        when(testYaml.getObjectKey()).thenReturn(objectKey);
        when(objectKey.getObjectField()).thenReturn("field");
        assertEquals("---\nkey: \"value\"\nobjectKey:\n  objectField: \"field\"\n", yamlUtils.write(testYaml));
    }
}
