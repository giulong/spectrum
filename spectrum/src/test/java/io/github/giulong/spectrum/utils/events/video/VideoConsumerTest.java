package io.github.giulong.spectrum.utils.events.video;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.ORIGINAL_DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.utils.web_driver_events.VideoAutoScreenshotProducer.SCREENSHOT;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class VideoConsumerTest {

    private static final int WIDTH = 11;
    private static final int HEIGHT = 11;
    private static final int EVEN_WIDTH = WIDTH + 1;
    private static final int EVEN_HEIGHT = HEIGHT + 1;

    private static MockedStatic<AWTSequenceEncoder> awtSequenceEncoderMockedStatic;
    private static MockedStatic<ImageIO> imageIOMockedStatic;
    private static MockedStatic<ByteArrayInputStream> byteArrayInputStreamMockedStatic;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private AWTSequenceEncoder encoder;

    @Mock
    private File videoFile;

    @Mock
    private BufferedImage bufferedImage;

    @Mock
    private Graphics2D graphics2D;

    @Mock
    private WebDriver driver;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Window window;

    @Mock
    private Dimension dimension;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private Path videoPath;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private Event event;

    @MockSingleton
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Map<String, Object> payload;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @InjectMocks
    private VideoConsumer videoConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("messageDigest", videoConsumer, messageDigest);

        awtSequenceEncoderMockedStatic = mockStatic(AWTSequenceEncoder.class);
        imageIOMockedStatic = mockStatic(ImageIO.class);
        byteArrayInputStreamMockedStatic = mockStatic(ByteArrayInputStream.class);
    }

    @AfterEach
    void afterEach() {
        awtSequenceEncoderMockedStatic.close();
        imageIOMockedStatic.close();
        byteArrayInputStreamMockedStatic.close();
    }

    @Test
    @DisplayName("accept should encode the image is new")
    void accept() throws IOException {
        final int width = 1;
        final int height = 3;

        acceptStubs();

        when(testData.getVideoPath()).thenReturn(videoPath);
        when(testData.getEncoders()).thenReturn(Map.of(videoPath, encoder));

        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(videoFile, 1)).thenReturn(encoder);

        when(video.isSkipDuplicateFrames()).thenReturn(true);
        when(store.get(ORIGINAL_DRIVER, WebDriver.class)).thenReturn(driver);

        // isNewFrame
        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final byte[] screenshotBytes = new byte[]{4, 5, 6};
        final byte[] newFrameDigest = new byte[]{7, 8, 9};
        when(testData.getLastFrameDigest()).thenReturn(lastFrameDigest);
        when(payload.get(SCREENSHOT)).thenReturn(screenshotBytes);
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(newFrameDigest);

        // chooseDimensionFor
        when(video.getWidth()).thenReturn(width);
        when(video.getHeight()).thenReturn(height);

        final MockedConstruction<ByteArrayInputStream> byteArrayInputStreamMockedConstruction = mockConstruction(ByteArrayInputStream.class, (mock, executionContext) -> {
            assertEquals(screenshotBytes, executionContext.arguments().getFirst());

            imageIOMockedStatic.when(() -> ImageIO.read(mock)).thenReturn(bufferedImage);
        });

        // resize
        final MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, executionContext) -> {
            assertEquals(width + 1, executionContext.arguments().getFirst());
            assertEquals(height + 1, executionContext.arguments().get(1));
            assertEquals(TYPE_INT_RGB, executionContext.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        videoConsumer.accept(event);

        final BufferedImage resizedImage = bufferedImageMockedConstruction.constructed().getFirst();
        verify(encoder).encodeImage(resizedImage);
        verifyNoMoreInteractions(encoder);

        bufferedImageMockedConstruction.close();
        byteArrayInputStreamMockedConstruction.close();
    }

    @Test
    @DisplayName("accept should not encode the image if it's a duplicate frame")
    void acceptDuplicateFrame() {
        acceptStubs();

        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(videoFile, 1)).thenReturn(encoder);

        when(video.isSkipDuplicateFrames()).thenReturn(true);

        // isNewFrame
        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final byte[] screenshotBytes = new byte[]{4, 5, 6};
        when(testData.getLastFrameDigest()).thenReturn(lastFrameDigest);
        when(payload.get(SCREENSHOT)).thenReturn(screenshotBytes);
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(lastFrameDigest);

        videoConsumer.accept(event);

        verifyNoInteractions(encoder);
    }

    @Test
    @DisplayName("accept should encode the image if duplicate frames should be skipped but the image is new")
    void acceptDuplicateFrameNewFrame() throws IOException {
        final int width = 1;
        final int height = 3;

        acceptStubs();

        when(testData.getVideoPath()).thenReturn(videoPath);
        when(testData.getEncoders()).thenReturn(Map.of(videoPath, encoder));

        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(videoFile, 1)).thenReturn(encoder);

        when(video.isSkipDuplicateFrames()).thenReturn(false);

        final byte[] screenshotBytes = new byte[]{4, 5, 6};
        when(payload.get(SCREENSHOT)).thenReturn(screenshotBytes);

        // chooseDimensionFor
        when(video.getWidth()).thenReturn(width);
        when(video.getHeight()).thenReturn(height);

        final MockedConstruction<ByteArrayInputStream> byteArrayInputStreamMockedConstruction = mockConstruction(ByteArrayInputStream.class, (mock, executionContext) -> {
            assertEquals(screenshotBytes, executionContext.arguments().getFirst());

            imageIOMockedStatic.when(() -> ImageIO.read(mock)).thenReturn(bufferedImage);
        });

        // resize
        final MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, executionContext) -> {
            assertEquals(width + 1, executionContext.arguments().getFirst());
            assertEquals(height + 1, executionContext.arguments().get(1));
            assertEquals(TYPE_INT_RGB, executionContext.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        videoConsumer.accept(event);

        final BufferedImage resizedImage = bufferedImageMockedConstruction.constructed().getFirst();
        verify(encoder).encodeImage(resizedImage);
        verifyNoMoreInteractions(encoder);

        bufferedImageMockedConstruction.close();
        byteArrayInputStreamMockedConstruction.close();
    }

    @Test
    @DisplayName("getVideoPathFrom should return the video path from the provided testData")
    void getVideoPathFrom() {
        when(testData.getVideoPath()).thenReturn(videoPath);

        assertEquals(videoPath, videoConsumer.getVideoPathFrom(testData));
    }

    @Test
    @DisplayName("chooseDimensionFor should return a new Dimension based on the provided video dimension")
    void chooseDimensionFor() {
        when(video.getWidth()).thenReturn(1);
        when(video.getHeight()).thenReturn(3);

        final Dimension actual = videoConsumer.chooseDimensionFor(driver, video);
        assertEquals(2, actual.getWidth());
        assertEquals(4, actual.getHeight());
    }

    @DisplayName("chooseDimensionFor should return a new Dimension based on webDriver's dimension, if at least one is lte 0")
    @ParameterizedTest(name = "with width {0} and height {1}")
    @MethodSource("dimensionProvider")
    void chooseDimensionForProvided(final int width, final int height) {
        final int menuBarsHeight = 123;

        reset(video);
        when(video.getWidth()).thenReturn(width);

        if (width >= 1) {   // short-circuit
            when(video.getHeight()).thenReturn(height);
        }
        when(driver.manage()).thenReturn(options);
        when(options.window()).thenReturn(window);
        when(window.getSize()).thenReturn(dimension);
        when(dimension.getWidth()).thenReturn(WIDTH);
        when(dimension.getHeight()).thenReturn(HEIGHT);
        when(video.getMenuBarsHeight()).thenReturn(menuBarsHeight);

        final Dimension actual = videoConsumer.chooseDimensionFor(driver, video);

        assertEquals(EVEN_WIDTH, actual.getWidth());
        assertEquals(EVEN_HEIGHT - menuBarsHeight - 1, actual.getHeight());
    }

    static Stream<Arguments> dimensionProvider() {
        return Stream.of(
                arguments(0, 0),
                arguments(1, 0),
                arguments(0, 1)
        );
    }

    @DisplayName("makeItEven should increment the provided int if it's odd")
    @ParameterizedTest(name = "with i {0} we expect {1}")
    @MethodSource("valuesProvider")
    void makeItEven(final int i, final int expected) {
        assertEquals(expected, videoConsumer.makeItEven(i));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(0, 0),
                arguments(1, 2)
        );
    }

    @Test
    @DisplayName("isNewFrame should return true if the provided screenshot is new")
    void isNewFrame() {
        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final byte[] screenshotBytes = new byte[]{4, 5, 6};
        final byte[] newFrameDigest = new byte[]{7, 8, 9};

        when(testData.getLastFrameDigest()).thenReturn(lastFrameDigest);

        //when(screenshot.getData()).thenReturn(screenshotBytes);
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(newFrameDigest);

        assertTrue(videoConsumer.isNewFrame(screenshotBytes, testData));

        assertArrayEquals(screenshotBytes, byteArrayArgumentCaptor.getValue());
        verify(testData).setLastFrameDigest(newFrameDigest);
    }

    @Test
    @DisplayName("isNewFrame should return false if the provided screenshot is not new")
    void isNewFrameFalse() {
        final byte[] lastFrameDigest = new byte[]{1, 2, 3};
        final byte[] screenshotBytes = new byte[]{4, 5, 6};

        when(testData.getLastFrameDigest()).thenReturn(lastFrameDigest);

        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(lastFrameDigest);

        assertFalse(videoConsumer.isNewFrame(screenshotBytes, testData));

        assertArrayEquals(screenshotBytes, byteArrayArgumentCaptor.getValue());
        verify(testData, never()).setLastFrameDigest(any());
    }

    @Test
    @DisplayName("resize should resize the image and return it")
    void resize() {
        final int width = 6;
        final int height = 100;

        MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, executionContext) -> {
            assertEquals(EVEN_WIDTH, executionContext.arguments().getFirst());
            assertEquals(EVEN_HEIGHT, executionContext.arguments().get(1));
            assertEquals(TYPE_INT_RGB, executionContext.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        when(dimension.getWidth()).thenReturn(EVEN_HEIGHT);
        when(dimension.getHeight()).thenReturn(EVEN_HEIGHT);
        when(bufferedImage.getWidth()).thenReturn(width);
        when(bufferedImage.getHeight()).thenReturn(height);

        final BufferedImage actual = videoConsumer.resize(bufferedImage, dimension);

        assertEquals(bufferedImageMockedConstruction.constructed().getFirst(), actual);
        verify(graphics2D).drawImage(bufferedImage, 0, 0, width, EVEN_HEIGHT, null);
        verify(graphics2D).dispose();

        bufferedImageMockedConstruction.close();
    }

    private void acceptStubs() {
        when(configuration.getVideo()).thenReturn(video);
        when(event.getPayload()).thenReturn(payload);
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
    }
}
