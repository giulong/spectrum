package com.giuliolongfils.spectrum.utils;

import com.giuliolongfils.spectrum.TestYaml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("YamlWriter")
class YamlWriterTest {

    @InjectMocks
    private YamlWriter yamlWriter;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        assertSame(YamlWriter.getInstance(), YamlWriter.getInstance());
    }

    @Test
    @DisplayName("write should just call the writeValueAsString of the provided object")
    public void write() {
        final TestYaml testYaml = mock(TestYaml.class);
        final TestYaml.ObjectKey objectKey = mock(TestYaml.ObjectKey.class);

        when(testYaml.getKey()).thenReturn("value");
        when(testYaml.getObjectKey()).thenReturn(objectKey);
        when(objectKey.getObjectField()).thenReturn("field");
        assertEquals("---\nkey: \"value\"\nobjectKey:\n  objectField: \"field\"\n", yamlWriter.write(testYaml));
    }
}