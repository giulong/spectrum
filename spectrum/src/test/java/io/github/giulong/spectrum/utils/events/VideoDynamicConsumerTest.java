package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.SpectrumEntity.HASH_ALGORITHM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class VideoDynamicConsumerTest {

    private static MockedStatic<Files> filesMockedStatic;

    @Mock
    private TestData testData;

    @Mock
    private Path dynamicVideoPath;

    @Mock
    private File screenshot;

    @Mock
    private Path screenshotPath;

    @Mock
    private MessageDigest messageDigest;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @InjectMocks
    private VideoDynamicConsumer videoDynamicConsumer;

    @BeforeEach
    public void beforeEach() throws IOException {
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    public void afterEach() {
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("init should init the needed fields")
    public void init() throws NoSuchAlgorithmException {
        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final String displayName = "displayName";

        Reflections.setField("lastFrameDigest", videoDynamicConsumer, lastFrameDigest);
        Reflections.setField("lastFrameDisplayName", videoDynamicConsumer, displayName);

        videoDynamicConsumer.init();

        assertNull(Reflections.getFieldValue("lastFrameDigest", videoDynamicConsumer));
        assertNull(Reflections.getFieldValue("lastFrameDisplayName", videoDynamicConsumer));
        assertEquals(MessageDigest.getInstance(HASH_ALGORITHM).getAlgorithm(), (Reflections.getFieldValue("messageDigest", videoDynamicConsumer, MessageDigest.class)).getAlgorithm());
    }

    @Test
    @DisplayName("getVideoPathFrom should return the dynamic video path from the provided testData")
    public void getVideoPathFrom() {
        when(testData.getDynamicVideoPath()).thenReturn(dynamicVideoPath);

        assertEquals(dynamicVideoPath, videoDynamicConsumer.getVideoPathFrom(testData));
    }

    @DisplayName("filter should return true if the provided file contains the displayName of the provided testData")
    @ParameterizedTest(name = "with file name {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void filter(final String fileName, final boolean expected) {
        final String displayName = "displayName";

        when(screenshot.getName()).thenReturn(fileName);
        lenient().when(testData.getDisplayName()).thenReturn(displayName);

        assertEquals(expected, videoDynamicConsumer.filter(screenshot, testData));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("abc-displayName-def12345-1234-1234-1234-123412345678.png", true),
                arguments("abc-notMatchingDisplayName-def12345-1234-1234-1234-123412345678.png", false),
                arguments("notMatchingAtAll", false)
        );
    }


    @Test
    @DisplayName("isNewFrame should return true if the display name of the provided testData is new")
    public void isNewFrame() {
        final String displayName = "displayName";

        when(testData.getDisplayName()).thenReturn(displayName);

        assertTrue(videoDynamicConsumer.isNewFrame(screenshot, testData));
    }

    @Test
    @DisplayName("isNewFrame should delegate to the parent's implementation when the displayName of the provided testData is not new")
    public void isNewFrameOld() throws IOException {
        final String displayName = "displayName";

        when(testData.getDisplayName()).thenReturn(displayName);

        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final byte[] screenshotBytes = new byte[]{4, 5, 6};
        final byte[] newFrameDigest = new byte[]{7, 8, 9};

        Reflections.setField("lastFrameDigest", videoDynamicConsumer, lastFrameDigest);
        Reflections.setField("lastFrameDisplayName", videoDynamicConsumer, displayName);

        when(screenshot.toPath()).thenReturn(screenshotPath);
        when(Files.readAllBytes(screenshotPath)).thenReturn(screenshotBytes);
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(newFrameDigest);

        assertTrue(videoDynamicConsumer.isNewFrame(screenshot, testData));

        assertArrayEquals(screenshotBytes, byteArrayArgumentCaptor.getValue());
        assertArrayEquals(newFrameDigest, Reflections.getFieldValue("lastFrameDigest", videoDynamicConsumer, byte[].class));
    }
}