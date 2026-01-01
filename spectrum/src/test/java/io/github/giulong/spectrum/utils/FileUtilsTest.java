package io.github.giulong.spectrum.utils;

import static io.github.giulong.spectrum.utils.FileUtils.HASH_ALGORITHM;
import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class FileUtilsTest {

    private final byte[] bytes = new byte[]{1, 2, 3};
    private final byte[] digest = new byte[]{4, 5, 6};

    private static MockedStatic<Files> filesMockedStatic;
    private MockedStatic<MessageDigest> messageDigestMockedStatic;

    @Mock
    private BasicFileAttributes basicFileAttributes;

    @Mock
    private FileTime creationTime;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private Path path;

    @Mock
    private Path parentPath;

    @Mock
    private TestData testData;

    @Mock
    private File file;

    @InjectMocks
    private FileUtils fileUtils;

    @BeforeEach
    void beforeEach() {
        filesMockedStatic = mockStatic();
        messageDigestMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        filesMockedStatic.close();
        messageDigestMockedStatic.close();
    }

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

    @Test
    @DisplayName("readBytesOf should return the correct result")
    void readBytesOf() throws IOException {
        when(Files.readAllBytes(path)).thenReturn(bytes);
        assertEquals(bytes, fileUtils.readBytesOf(path));
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
                arguments("fileName-${timestamp:dd-MM-yyyy}.html", "fileName-[0-9]{2}-[0-9]{2}-[0-9]{4}.html"));
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
                arguments("fileName.", ""));
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
                arguments("fileName.", "fileName"));
    }

    @Test
    @DisplayName("delete should do nothing if the provided folder doesn't exist and return the reference to it")
    void deleteNotExisting() {
        when(Files.notExists(path)).thenReturn(true);

        assertEquals(path, fileUtils.delete(path));

        //noinspection resource
        filesMockedStatic.verify(() -> Files.walk(path), never());
    }

    @Test
    @DisplayName("delete should delete the provided folder and return the reference to it")
    void delete() throws IOException {
        final Path filePath = mock();

        when(Files.notExists(path)).thenReturn(false);
        when(Files.walk(path)).thenReturn(Stream.of(filePath));
        when(filePath.toFile()).thenReturn(file);

        assertEquals(path, fileUtils.delete(path));

        verify(file).delete();
    }

    @Test
    @DisplayName("overloaded delete that takes a file should delegate to the one taking its path")
    void deleteFile() throws IOException {
        final Path filePath = mock();

        when(file.toPath()).thenReturn(path);
        when(Files.notExists(path)).thenReturn(false);
        when(Files.walk(path)).thenReturn(Stream.of(filePath));
        when(filePath.toFile()).thenReturn(file);

        assertEquals(path, fileUtils.delete(file));

        verify(file).delete();
    }

    @Test
    @DisplayName("delete should delete the provided directory, recreate it, and return the reference to it")
    void deleteContentOf() throws IOException {
        final Path filePath = mock();

        when(Files.createDirectories(path)).thenReturn(path);

        when(Files.notExists(path)).thenReturn(false);
        when(Files.walk(path)).thenReturn(Stream.of(filePath));
        when(filePath.toFile()).thenReturn(file);

        assertEquals(path, fileUtils.deleteContentOf(path));
        verify(file).delete();
    }

    @Test
    @DisplayName("write should write the provided content to a file in the provided path, creating the parent folders if needed")
    void write() {
        final String content = "content";

        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(file);

        fileUtils.write(path, content);

        verify(file).mkdirs();
        filesMockedStatic.verify(() -> Files.write(path, content.getBytes()));
    }

    @Test
    @DisplayName("write should write the provided content to a file in the provided string path, creating the parent folders if needed")
    void writeString() {
        final MockedStatic<Path> pathMockedStatic = mockStatic();
        final String stringPath = "stringPath";
        final String content = "content";

        when(Path.of(stringPath)).thenReturn(path);
        when(path.getParent()).thenReturn(parentPath);
        when(parentPath.toFile()).thenReturn(file);

        fileUtils.write(stringPath, content);

        verify(file).mkdirs();
        filesMockedStatic.verify(() -> Files.write(path, content.getBytes()));

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
        when(file.toPath()).thenReturn(path);
        when(Files.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.creationTime()).thenReturn(creationTime);

        assertEquals(creationTime, fileUtils.getCreationTimeOf(file));
    }

    @Test
    @DisplayName("createTempFile should create a temp file with the provided prefix, suffix, and delete it on exit")
    void createTempFile() throws IOException {
        final String prefix = "prefix";
        final String suffix = "suffix";

        when(Files.createTempFile(prefix, suffix)).thenReturn(path);
        when(path.toFile()).thenReturn(file);

        assertEquals(path, fileUtils.createTempFile(prefix, suffix));

        verify(file).deleteOnExit();
    }

    @Test
    @DisplayName("getScreenshotNameFrom should return the name for the provided testData")
    void getScreenshotNameFrom() {
        when(testData.getScreenshotNumber()).thenReturn(123);

        assertEquals("screenshot-123.png", fileUtils.getScreenshotNameFrom(testData));
    }

    @Test
    @DisplayName("getFailedScreenshotNameFrom should return the name for the provided testData, with the 'failed' suffix")
    void getFailedScreenshotNameFrom() {
        when(testData.getScreenshotNumber()).thenReturn(123);

        assertEquals("screenshot-123-failed.png", fileUtils.getFailedScreenshotNameFrom(testData));
    }

    @Test
    @DisplayName("getScreenshotsDiffNameFrom should return the name for the provided testData, with the 'diff' suffix")
    void getScreenshotsDiffNameFrom() {
        when(testData.getScreenshotNumber()).thenReturn(123);

        assertEquals("screenshot-123-diff.png", fileUtils.getScreenshotsDiffNameFrom(testData));
    }

    @Test
    @DisplayName("checksumOf should return the byte array of the sha digest of the provided file")
    void checksumOfBytes() throws NoSuchAlgorithmException {
        when(MessageDigest.getInstance(HASH_ALGORITHM)).thenReturn(messageDigest);
        when(messageDigest.digest(bytes)).thenReturn(digest);

        assertArrayEquals(digest, fileUtils.checksumOf(bytes));
    }

    @ParameterizedTest(name = "with digest arrays {0} and {1} we expect {2}")
    @DisplayName("compare should check if the two provided byte arrays are the same")
    @MethodSource("compareValuesProvider")
    void compare(final byte[] digest1, final byte[] digest2, final boolean expected) throws NoSuchAlgorithmException {
        when(MessageDigest.getInstance(HASH_ALGORITHM)).thenReturn(messageDigest);
        when(messageDigest.digest(bytes))
                .thenReturn(digest1)
                .thenReturn(digest2);

        assertEquals(expected, fileUtils.compare(bytes, bytes));
    }

    static Stream<Arguments> compareValuesProvider() {
        return Stream.of(
                arguments(new byte[]{1}, new byte[]{1}, true),
                arguments(new byte[]{1}, new byte[]{2}, false)
        );
    }

    @ParameterizedTest(name = "with digest arrays {0} and {1} we expect {2}")
    @DisplayName("compare should check if the files at the two provided paths are the same")
    @MethodSource("compareValuesProvider")
    void comparePaths(final byte[] digest1, final byte[] digest2, final boolean expected) throws NoSuchAlgorithmException, IOException {
        when(MessageDigest.getInstance(HASH_ALGORITHM)).thenReturn(messageDigest);
        when(Files.readAllBytes(path)).thenReturn(bytes);
        when(messageDigest.digest(bytes))
                .thenReturn(digest1)
                .thenReturn(digest2);

        assertEquals(expected, fileUtils.compare(path, path));
    }

    @ParameterizedTest(name = "with digest arrays {0} and {1} we expect {2}")
    @DisplayName("compare should check if the file at the provided path and the provided byte array are the same")
    @MethodSource("compareValuesProvider")
    void compareMixed(final byte[] digest1, final byte[] digest2, final boolean expected) throws NoSuchAlgorithmException, IOException {
        when(MessageDigest.getInstance(HASH_ALGORITHM)).thenReturn(messageDigest);
        when(Files.readAllBytes(path)).thenReturn(bytes);
        when(messageDigest.digest(bytes))
                .thenReturn(digest1)
                .thenReturn(digest2);

        assertEquals(expected, fileUtils.compare(path, bytes));
    }
}
