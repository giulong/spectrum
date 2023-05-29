package com.github.giulong.spectrum.utils;

import com.github.giulong.spectrum.TestYaml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("YamlParser")
class YamlParserTest {

    @InjectMocks
    private YamlParser yamlParser;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        assertSame(YamlParser.getInstance(), YamlParser.getInstance());
    }

    @Test
    @DisplayName("read should return null if the provided client file doesn't exist")
    public void readNotExistingClientFile() {
        assertNull(yamlParser.read("not-existing", TestYaml.class, false));
    }

    @Test
    @DisplayName("read should return an instance of the provided class deserializing the provided file")
    public void read() {
        assertEquals("value", Objects.requireNonNull(yamlParser.read("test.yaml", TestYaml.class, true)).getKey());
    }

    @Test
    @DisplayName("overloaded read should return an instance of the provided class deserializing the provided file")
    public void readClient() {
        assertEquals("value", yamlParser.read("test.yaml", TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readInternal should return an instance of the provided class deserializing the provided file")
    public void readInternal() {
        assertEquals("value", yamlParser.readInternal("test.yaml", TestYaml.class).getKey());
    }

    @Test
    @DisplayName("readNode should return null if the provided client file doesn't exist")
    public void readNotExistingClientNode() {
        assertNull(yamlParser.readNode("/objectKey", "not-existing", TestYaml.ObjectKey.class, false));
    }

    @Test
    @DisplayName("readNode should check if the provided file exists and return the node requested")
    public void readNode() {
        assertEquals("objectValue", Objects.requireNonNull(yamlParser.readNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class, true)).getObjectField());
    }

    @Test
    @DisplayName("readNode for client-side files should just delegate to the readNode method")
    public void readClientNode() {
        assertEquals("objectValue", yamlParser.readNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class).getObjectField());
    }

    @Test
    @DisplayName("readInternalNode should just delegate to the readNode method")
    public void readInternalNode() {
        assertEquals("objectValue", yamlParser.readInternalNode("/objectKey", "test.yaml", TestYaml.ObjectKey.class).getObjectField());
    }

    @Test
    @DisplayName("update should update the provided instance with the string content provided")
    public void update() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlParser.update(testYaml, "{ \"key\": \"value\" }");
        assertEquals("value", testYaml.getKey());
    }

    @Test
    @DisplayName("updateWithFile should update the provided instance with the file provided")
    public void updateWithFile() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlParser.updateWithFile(testYaml, "test.yaml");
        assertEquals("value", testYaml.getKey());
    }

    @Test
    @DisplayName("updateWithFile should do nothing if the provided file doesn't exist")
    public void updateWithNotExistingFile() {
        TestYaml testYaml = TestYaml.builder().key("original").build();

        yamlParser.updateWithFile(testYaml, "not-existing");
        assertEquals("original", testYaml.getKey());
    }
}