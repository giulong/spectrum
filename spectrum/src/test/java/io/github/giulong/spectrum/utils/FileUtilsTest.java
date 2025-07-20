package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class FileUtilsTest {

    private static final String DISPLAY_NAME = "displayName";
    private static final String UUID_REGEX = DISPLAY_NAME + "-([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.png";

    @Mock
    private BasicFileAttributes basicFileAttributes;

    @Mock
    private FileTime creationTime;

    @Mock
    private Path path;

    @Mock
    private Path parentPath;

    @Mock
    private File file;

    @Mock
    private StatefulExtentTest statefulExtentTest;

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
    void read(final String template, final String expected) {
        assertEquals(expected, fileUtils.read(template));
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
    void readTemplate(final String template, final String expected) {
        assertEquals(expected, fileUtils.readTemplate(template));
    }

    static Stream<Arguments> readTemplateValuesProvider() {
        return Stream.of(
                arguments("template-test.yaml", "key: value" + lineSeparator() + "objectKey:" + lineSeparator() +
                        "  objectField: objectValue" + lineSeparator() + "internalKey:" + lineSeparator() +
                        "  field: ignored"),
                arguments("not-existing", ""));
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

    @DisplayName("getExtensionWithDotOf should return the extension with dot of the provided fileName")
    @ParameterizedTest(name = "with fileName {0} we expect {1}")
    @MethodSource("getExtensionWithDotOfValuesProvider")
    void getExtensionWithDotOf(final String fileName, final String expected) {
        assertEquals(expected, fileUtils.getExtensionWithDotOf(fileName));
    }

    static Stream<Arguments> getExtensionWithDotOfValuesProvider() {
        return Stream.of(
                arguments("fileName.abc", ".abc"),
                arguments("fileName", "fileName"),
                arguments("fileName.", ".")
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

    @DisplayName("delete should delete the provided folder and return the reference to it")
    @ParameterizedTest(name = "with value {0} which is existing? {1}")
    @MethodSource("deleteValuesProvider")
    void delete(final Path directory, final boolean existing) {
        directory.toFile().deleteOnExit();
        assertEquals(existing, Files.exists(directory));

        assertEquals(directory, fileUtils.delete(directory));

        assertFalse(Files.exists(directory));
    }

    static Stream<Arguments> deleteValuesProvider() throws IOException {
        return Stream.of(
                arguments(Path.of("abc not existing"), false),
                arguments(Files.createTempDirectory("downloadsFolder"), true));
    }

    @Test
    @DisplayName("overloaded delete that takes a file should delegate to the one taking its path")
    void deleteFile() throws IOException {
        final File directory = Files.createTempDirectory("downloadsFolder").toFile();
        directory.deleteOnExit();

        assertEquals(directory.toPath(), fileUtils.delete(directory));

        assertFalse(directory.exists());
    }

    @DisplayName("delete should delete the provided directory, recreate it, and return the reference to it")
    @ParameterizedTest(name = "with value {0} which is existing? {1}")
    @MethodSource("deleteValuesProvider")
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

    @Test
    @DisplayName("getCreationTimeOf should return the creation time of the provided file")
    void getCreationTimeOf() throws IOException {
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);

        when(file.toPath()).thenReturn(path);
        when(Files.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.creationTime()).thenReturn(creationTime);

        assertEquals(creationTime, fileUtils.getCreationTimeOf(file));

        filesMockedStatic.close();
    }

    @Test
    @DisplayName("buildScreenshotNameFrom should return the name for the provided frame and display name")
    void buildScreenshotNameFrom() {
        when(statefulExtentTest.getDisplayName()).thenReturn(DISPLAY_NAME);

        assertThat(fileUtils.buildScreenshotNameFrom(statefulExtentTest), matchesPattern(UUID_REGEX));
    }
}
