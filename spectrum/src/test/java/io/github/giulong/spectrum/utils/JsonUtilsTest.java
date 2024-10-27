package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonUtilsTest {

    @Mock
    private File file;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private ObjectWriter jsonWriter;

    @InjectMocks
    private JsonUtils jsonUtils;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("jsonMapper", jsonUtils, jsonMapper);
        Reflections.setField("writer", jsonUtils, jsonWriter);
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(JsonUtils.getInstance(), JsonUtils.getInstance());
    }

    @Test
    @DisplayName("read should deserialize the provided file onto an instance of the provided class")
    void read() throws IOException {
        final String expected = "expected";
        when(file.exists()).thenReturn(true);
        when(jsonMapper.readValue(file, String.class)).thenReturn(expected);

        assertEquals(expected, jsonUtils.readOrEmpty(file, String.class));
    }

    @Test
    @DisplayName("read should deserialize an empty json onto an instance of the provided class when the provided file doesn't exist")
    void readNotExisting() throws IOException {
        final String expected = "expected";
        when(file.exists()).thenReturn(false);
        when(jsonMapper.readValue(eq("{}"), eq(String.class))).thenReturn(expected);

        assertEquals(expected, jsonUtils.readOrEmpty(file, String.class));
    }

    @Test
    @DisplayName("write should write the provided object")
    void write() throws JsonProcessingException {
        final Object object = mock(Object.class);
        final String expected = "expected";

        when(jsonWriter.writeValueAsString(object)).thenReturn(expected);

        assertEquals(expected, jsonUtils.write(object));
    }
}
