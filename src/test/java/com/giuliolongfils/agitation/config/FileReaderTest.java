package com.giuliolongfils.agitation.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileReader")
class FileReaderTest {

    @InjectMocks
    private FileReader fileReader;

    @DisplayName("read should return the correct result")
    @ParameterizedTest(name = "reading file {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void read(String file, String expected) {
        assertEquals(expected, fileReader.read(file));
    }

    @Test
    @DisplayName("readProperties should read the provided file and return the corresponding properties instance")
    public void readProperties() {
        Properties actual = fileReader.readProperties("/test.properties");
        assertEquals(1, actual.size());
        assertEquals("value", actual.getProperty("key"));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("/test.yaml", "key: value"),
                arguments("not-existing", null));
    }
}