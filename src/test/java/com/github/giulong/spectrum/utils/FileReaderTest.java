package com.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileReader")
class FileReaderTest {

    @InjectMocks
    private FileReader fileReader;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        assertSame(FileReader.getInstance(), FileReader.getInstance());
    }

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

    @Test
    @DisplayName("readProperties should throw an exception if the provided file doesn't exist")
    public void readPropertiesNotExisting() {
        assertThrows(RuntimeException.class, () -> fileReader.readProperties("/not-existing"));
    }

    @Test
    @DisplayName("interpolate should return the provided file with the placeholder replaced with vars from the provided map")
    public void interpolate() {
        assertEquals(
                "key: value" + lineSeparator() + "objectKey:" + lineSeparator() + "  objectField: objectValue",
                fileReader.interpolate("/interpolate.yaml", Map.of("{{value}}", "value", "{{objectValue}}", "objectValue")));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("/test.yaml", "key: value" + lineSeparator() + "objectKey:" + lineSeparator() + "  objectField: objectValue"),
                arguments("not-existing", ""));
    }
}