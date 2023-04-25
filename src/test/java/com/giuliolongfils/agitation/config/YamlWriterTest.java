package com.giuliolongfils.agitation.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.giuliolongfils.agitation.TestYaml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@DisplayName("YamlWriter")
class YamlWriterTest {

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        assertSame(YamlWriter.getInstance(), YamlWriter.getInstance());
    }

    @Test
    @DisplayName("write should just call the writeValueAsString of the provided object")
    public void write() throws JsonProcessingException {
        ObjectWriter objectWriter = mock(ObjectWriter.class);

        try (MockedConstruction<YAMLMapper> ignored = mockConstruction(YAMLMapper.class, (mock, context) -> {
            when(mock.configure(FAIL_ON_EMPTY_BEANS, false)).thenReturn(mock);
            when(mock.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        })) {
            YamlWriter yamlWriter = new YamlWriter();
            TestYaml testYaml = mock(TestYaml.class);
            yamlWriter.write(testYaml);

            verify(objectWriter).writeValueAsString(testYaml);
        }
    }
}