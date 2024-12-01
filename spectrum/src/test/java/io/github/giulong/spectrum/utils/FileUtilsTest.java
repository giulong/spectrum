package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;

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

class FileUtilsTest {

    @InjectMocks
    private FileUtils fileUtils;

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(FileUtils.getInstance(), FileUtils.getInstance());
    }

    @DisplayName("read should return the correct result")
    @ParameterizedTest(name = "reading file {0} we expect {1}")
    @MethodSource("valuesProvider")
    void read(String file, String expected) {
        assertEquals(expected, fileUtils.read(file));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("test.yaml", "key: value" + lineSeparator() + "objectKey:" + lineSeparator() +
                        "  objectField: objectValue" + lineSeparator() + "internalKey:" + lineSeparator() +
                        "  field: ignored"),
                arguments("not-existing", ""));
    }

    @DisplayName("readTemplate should return the correct result")
    @ParameterizedTest(name = "reading file {0} we expect {1}")
    @MethodSource("readTemplateValuesProvider")
    void readTemplate(String file, String expected) {
        assertEquals(expected, fileUtils.readTemplate(file));
    }

    static Stream<Arguments> readTemplateValuesProvider() {
        return Stream.of(
                arguments("template-test.yaml", "key: value" + lineSeparator() + "objectKey:" + lineSeparator() +
                        "  objectField: objectValue" + lineSeparator() + "internalKey:" + lineSeparator() +
                        "  field: ignored"),
                arguments("not-existing", ""));
    }

    @Test
    @DisplayName("interpolate should return the provided file with the placeholder replaced with vars from the provided map")
    void interpolate() {
        assertEquals(
                "key: value" + lineSeparator() + "objectKey:" + lineSeparator() + "  objectField: objectValue",
                fileUtils.interpolate("interpolate.yaml", Map.of("{{value}}", "value", "{{objectValue}}", "objectValue")));
    }

    @DisplayName("interpolateTimestampFrom should replace the timestamp from the provided file name")
    @ParameterizedTest(name = "with fileName {0} we expect {2}")
    @MethodSource("fileNamesProvider")
    void interpolateTimestampFrom(final String fileName, final String expected) {
        assertThat(fileUtils.interpolateTimestampFrom(fileName), matchesPattern(expected));
    }

    static Stream<Arguments> fileNamesProvider() {
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
    void getExtensionOf(final String fileName, final String expected) {
        assertEquals(expected, fileUtils.getExtensionOf(fileName));
    }

    static Stream<Arguments> getExtensionOfValuesProvider() {
        return Stream.of(
                arguments("fileName.abc", "abc"),
                arguments("fileName", "fileName"),
                arguments("fileName.", "")
        );
    }

    @DisplayName("removeExtensionFrom should return the provided fileName without the extension")
    @ParameterizedTest(name = "with fileName {0} we expect {1}")
    @MethodSource("removeExtensionFromValuesProvider")
    void removeExtensionFrom(final String fileName, final String expected) {
        assertEquals(expected, fileUtils.removeExtensionFrom(fileName));
    }

    static Stream<Arguments> removeExtensionFromValuesProvider() {
        return Stream.of(
                arguments("fileName.abc", "fileName"),
                arguments("fileName", "fileName"),
                arguments("fileName.", "fileName")
        );
    }

    @DisplayName("deleteDirectory should delete the provided folder and return the reference to it")
    @ParameterizedTest(name = "with value {0} which is existing? {1}")
    @MethodSource("deleteDirectoryValuesProvider")
    void deleteDirectory(final Path directory, final boolean existing) {
        directory.toFile().deleteOnExit();
        assertEquals(existing, Files.exists(directory));

        assertEquals(directory, fileUtils.deleteDirectory(directory));

        assertFalse(Files.exists(directory));
    }

    static Stream<Arguments> deleteDirectoryValuesProvider() throws IOException {
        return Stream.of(
                arguments(Path.of("abc not existing"), false),
                arguments(Files.createTempDirectory("downloadsFolder"), true));
    }

    @DisplayName("deleteContentOf should delete the provided directory, recreate it, and return the reference to it")
    @ParameterizedTest(name = "with value {0} which is existing? {1}")
    @MethodSource("deleteDirectoryValuesProvider")
    void deleteContentOf(final Path directory, final boolean existing) {
        directory.toFile().deleteOnExit();
        assertEquals(existing, Files.exists(directory));

        assertEquals(directory, fileUtils.deleteContentOf(directory));

        assertTrue(Files.exists(directory));
    }

    @Test
    @DisplayName("write should write the provided content to a file in the provided path, creating the parent folders if needed")
    void write() {
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
        final Path path = mock();
        final Path parentPath = mock();
        final File file = mock();
        final String content = "content";

        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(file);

        fileUtils.write(path, content);

        verify(file).mkdirs();
        filesMockedStatic.verify(() -> Files.write(path, content.getBytes()));

        filesMockedStatic.close();
    }

    @Test
    @DisplayName("write should write the provided content to a file in the provided string path, creating the parent folders if needed")
    void writeString() {
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
        final MockedStatic<Path> pathMockedStatic = mockStatic(Path.class);
        final String stringPath = "stringPath";
        final Path path = mock();
        final Path parentPath = mock();
        final File file = mock();
        final String content = "content";

        when(Path.of(stringPath)).thenReturn(path);
        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(file);

        fileUtils.write(stringPath, content);

        verify(file).mkdirs();
        filesMockedStatic.verify(() -> Files.write(path, content.getBytes()));

        filesMockedStatic.close();
        pathMockedStatic.close();
    }

    @Test
    @DisplayName("sanitize should strip the illegal chars from the provided string")
    void sanitize() {
        final String name = "hello123 /\\*][ -+.";
        final String sanitizedName = "hello123 ][ -+.";

        assertEquals(sanitizedName, fileUtils.sanitize(name));
    }
}
