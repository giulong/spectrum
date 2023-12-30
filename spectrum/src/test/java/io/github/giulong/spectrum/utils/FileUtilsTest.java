package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileUtils")
class FileUtilsTest {

    @InjectMocks
    private FileUtils fileUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(FileUtils.getInstance(), FileUtils.getInstance());
    }

    @DisplayName("read should return the correct result")
    @ParameterizedTest(name = "reading file {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void read(String file, String expected) {
        assertEquals(expected, fileUtils.read(file));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("/test.yaml", "key: value" + lineSeparator() + "objectKey:" + lineSeparator() + "  objectField: objectValue" + lineSeparator() + "internalKey:" + lineSeparator() + "  field: ignored"),
                arguments("not-existing", ""));
    }

    @DisplayName("readTemplate should return the correct result")
    @ParameterizedTest(name = "reading file {0} we expect {1}")
    @MethodSource("readTemplateValuesProvider")
    public void readTemplate(String file, String expected) {
        assertEquals(expected, fileUtils.readTemplate(file));
    }

    public static Stream<Arguments> readTemplateValuesProvider() {
        return Stream.of(
                arguments("template-test.yaml", "key: value" + lineSeparator() + "objectKey:" + lineSeparator() + "  objectField: objectValue" + lineSeparator() + "internalKey:" + lineSeparator() + "  field: ignored"),
                arguments("not-existing", ""));
    }

    @Test
    @DisplayName("interpolate should return the provided file with the placeholder replaced with vars from the provided map")
    public void interpolate() {
        assertEquals(
                "key: value" + lineSeparator() + "objectKey:" + lineSeparator() + "  objectField: objectValue",
                fileUtils.interpolate("/interpolate.yaml", Map.of("{{value}}", "value", "{{objectValue}}", "objectValue")));
    }

    @DisplayName("interpolateTimestampFrom should replace the timestamp from the provided file name")
    @ParameterizedTest(name = "with fileName {0} we expect {2}")
    @MethodSource("fileNamesProvider")
    public void interpolateTimestampFrom(final String fileName, final String expected) {
        assertThat(fileUtils.interpolateTimestampFrom(fileName), matchesPattern(expected));
    }

    public static Stream<Arguments> fileNamesProvider() {
        return Stream.of(
                arguments("fileName.html", "fileName.html"),
                arguments("fileName-${timestamp}.html", "fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}.html"),
                arguments("fileName-${timestamp:dd-MM-yyyy_HH-mm-ss}.html", "fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}.html"),
                arguments("fileName-${timestamp:dd-MM-yyyy}.html", "fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}.html")
        );
    }

    @DisplayName("getExtensionOf should return the extension of the provided fileName")
    @ParameterizedTest(name = "with fileName {0} we expect {1}")
    @MethodSource("getExtensionOfValuesProvider")
    public void getExtensionOf(final String fileName, final String expected) {
        assertEquals(expected, fileUtils.getExtensionOf(fileName));
    }

    public static Stream<Arguments> getExtensionOfValuesProvider() {
        return Stream.of(
                arguments("fileName.abc", "abc"),
                arguments("fileName", "fileName"),
                arguments("fileName.", "")
        );
    }

    @DisplayName("removeExtensionFrom should return the provided fileName without the extension")
    @ParameterizedTest(name = "with fileName {0} we expect {1}")
    @MethodSource("removeExtensionFromValuesProvider")
    public void removeExtensionFrom(final String fileName, final String expected) {
        assertEquals(expected, fileUtils.removeExtensionFrom(fileName));
    }

    public static Stream<Arguments> removeExtensionFromValuesProvider() {
        return Stream.of(
                arguments("fileName.abc", "fileName"),
                arguments("fileName", "fileName"),
                arguments("fileName.", "fileName")
        );
    }

    @DisplayName("deleteDirectory should delete and recreate the provided folder")
    @ParameterizedTest(name = "with value {0} which is existing? {1}")
    @MethodSource("deleteDirectoryValuesProvider")
    public void deleteDirectory(final Path directory, final boolean existing) {
        directory.toFile().deleteOnExit();
        assertEquals(existing, Files.exists(directory));

        fileUtils.deleteDirectory(directory);

        assertFalse(Files.exists(directory));
    }

    public static Stream<Arguments> deleteDirectoryValuesProvider() throws IOException {
        return Stream.of(
                arguments(Path.of("abc not existing"), false),
                arguments(Files.createTempDirectory("downloadsFolder"), true));
    }

    @Test
    @DisplayName("write should write the provided content to a file in the provided path, creating the parent folders if needed")
    public void write() {
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
        final Path path = mock(Path.class);
        final Path parentPath = mock(Path.class);
        final File file = mock(File.class);
        final String content = "content";

        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(file);

        fileUtils.write(path, content);

        verify(file).mkdirs();
        filesMockedStatic.verify(() -> Files.write(path, content.getBytes()));

        filesMockedStatic.close();
    }
}
