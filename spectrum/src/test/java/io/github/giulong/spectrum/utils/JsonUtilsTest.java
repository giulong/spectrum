package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsonUtils")
class JsonUtilsTest {

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private ObjectWriter jsonWriter;

    @InjectMocks
    private JsonUtils jsonUtils;

    @BeforeEach
    public void beforeEach() {
        ReflectionUtils.setField("jsonMapper", jsonUtils, jsonMapper);
        ReflectionUtils.setField("writer", jsonUtils, jsonWriter);
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(JsonUtils.getInstance(), JsonUtils.getInstance());
    }

    @Test
    @DisplayName("write should write the provided object")
    public void write() throws JsonProcessingException {
        final Object object = mock(Object.class);
        final String expected = "expected";

        when(jsonWriter.writeValueAsString(object)).thenReturn(expected);

        assertEquals(expected, jsonUtils.write(object));;
    }
}
