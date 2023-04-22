package com.giuliolongfils.agitation.config;

import com.giuliolongfils.agitation.TestYaml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("YamlParser")
class YamlParserTest {

    @InjectMocks
    private YamlParser yamlParser;

    @Test
    @DisplayName("read should return null if the provided client file doesn't exist")
    public void readNotExistingClientFile() {
        assertNull(yamlParser.read("not-existing", TestYaml.class, false));
    }

    @Test
    @DisplayName("read should return an instance of the provided class deserializing the provided file")
    public void read() {
        assertEquals("value", yamlParser.read("test.yaml", TestYaml.class, true).getKey());
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