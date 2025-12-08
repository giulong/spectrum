package io.github.giulong.spectrum.internals.jackson.deserializers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.YamlUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class DynamicDeserializerTest {

    private static MockedStatic<YamlUtils> yamlUtilsMockedStatic;

    @Mock
    private YamlUtils yamlUtils;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @Mock
    private JsonNode jsonNode;

    @InjectMocks
    private DynamicDeserializer<String> dynamicDeserializer;

    @BeforeEach
    void beforeEach() {
        yamlUtilsMockedStatic = mockStatic(YamlUtils.class);
    }

    @AfterEach
    void afterEach() {
        yamlUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("deserialize should return the class loaded from the provided string fqdn literal")
    void deserialize() throws IOException {
        final String expected = "expected";
        final String configFile = "configFile";
        Reflections.setField("configFile", dynamicDeserializer, configFile);
        Reflections.setField("clazz", dynamicDeserializer, String.class);

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        when(yamlUtils.readDynamicDeserializable(configFile, String.class, jsonNode)).thenReturn(expected);

        assertEquals(expected, dynamicDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
