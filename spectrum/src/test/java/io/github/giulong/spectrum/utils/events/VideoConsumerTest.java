package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.video.Video;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoConsumer")
class VideoConsumerTest {

    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";
    private static final int WIDTH = 11;
    private static final int HEIGHT = 11;
    private static final int EVEN_WIDTH = WIDTH + 1;
    private static final int EVEN_HEIGHT = HEIGHT + 1;

    private static MockedStatic<AWTSequenceEncoder> awtSequenceEncoderMockedStatic;
    private static MockedStatic<ImageIO> imageIOMockedStatic;
    private static MockedStatic<Files> filesMockedStatic;

    @Mock
    private AWTSequenceEncoder encoder;

    @Mock
    private File videoFile;

    @Mock
    private BufferedImage bufferedImage;

    @Mock
    private Graphics2D graphics2D;

    @Mock
    private WebDriver webDriver;

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
    private Path screenshotFolderPath;

    @Mock
    private Path screenshotPath1;

    @Mock
    private Path screenshotPath2;

    @Mock
    private Path screenshotPath3;

    @Mock
    private File screenshot1;

    @Mock
    private File screenshot2;

    @Mock
    private File screenshot3;

    @Mock
    private Path videoPath;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Event event;

    @Mock
    private Configuration configuration;

    @Mock
    private Set<byte[]> frameDigests;

    @Mock
    private Stream<byte[]> frameDigestsStream;

    @Captor
    private ArgumentCaptor<URL> urlArgumentCaptor;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @InjectMocks
    private VideoConsumer videoConsumer;

    @BeforeEach
    public void beforeEach() throws IOException {
        awtSequenceEncoderMockedStatic = mockStatic(AWTSequenceEncoder.class);
        imageIOMockedStatic = mockStatic(ImageIO.class);
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    public void afterEach() {
        awtSequenceEncoderMockedStatic.close();
        imageIOMockedStatic.close();
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("accept should notify the VideoEncoder that the test is done")
    public void accept() throws IOException {
        final int width = 1;
        final int height = 3;

        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getClassName()).thenReturn(CLASS_NAME);
        when(testData.getMethodName()).thenReturn(METHOD_NAME);
        when(testData.getScreenshotFolderPath()).thenReturn(screenshotFolderPath);
        when(testData.getVideoPath()).thenReturn(videoPath);
        when(videoPath.toFile()).thenReturn(videoFile);
        when(Files.walk(screenshotFolderPath)).thenReturn(Stream.of(screenshotPath1, screenshotPath2, screenshotPath3));
        when(screenshotPath1.toFile()).thenReturn(screenshot1);
        when(screenshotPath2.toFile()).thenReturn(screenshot2);
        when(screenshotPath3.toFile()).thenReturn(screenshot3);
        when(screenshot1.isFile()).thenReturn(true);
        when(screenshot2.isFile()).thenReturn(false);
        when(screenshot3.isFile()).thenReturn(true);

        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(videoFile, 1)).thenReturn(encoder);
        imageIOMockedStatic.when(() -> ImageIO.read(screenshot1)).thenReturn(bufferedImage);
        imageIOMockedStatic.when(() -> ImageIO.read(screenshot3)).thenReturn(bufferedImage);

        // isNewFrame 1
        when(screenshot1.toPath()).thenReturn(screenshotPath1);
        when(Files.readAllBytes(screenshotPath1)).thenReturn(new byte[]{1});
        // isNewFrame 3
        when(screenshot3.toPath()).thenReturn(screenshotPath3);
        when(Files.readAllBytes(screenshotPath3)).thenReturn(new byte[]{3});

        // chooseDimensionFor
        when(video.getWidth()).thenReturn(width);
        when(video.getHeight()).thenReturn(height);

        // resize
        MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, context) -> {
            assertEquals(width + 1, context.arguments().getFirst());
            assertEquals(height + 1, context.arguments().get(1));
            assertEquals(TYPE_INT_RGB, context.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        videoConsumer.accept(event);

        final BufferedImage resizedImage1 = bufferedImageMockedConstruction.constructed().getFirst();
        final BufferedImage resizedImage2 = bufferedImageMockedConstruction.constructed().get(1);
        verify(encoder).encodeImage(resizedImage1);
        verify(encoder).encodeImage(resizedImage2);
        verify(encoder).finish();

        bufferedImageMockedConstruction.close();
    }

    @Test
    @DisplayName("accept should add the no-video.png if no frames were added")
    public void acceptNoFramesAdded() throws IOException {
        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getClassName()).thenReturn(CLASS_NAME);
        when(testData.getMethodName()).thenReturn(METHOD_NAME);
        when(testData.getScreenshotFolderPath()).thenReturn(screenshotFolderPath);
        when(testData.getVideoPath()).thenReturn(videoPath);
        when(videoPath.toFile()).thenReturn(videoFile);
        when(Files.walk(screenshotFolderPath)).thenReturn(Stream.of());

        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(videoFile, 1)).thenReturn(encoder);
        imageIOMockedStatic.when(() -> ImageIO.read(urlArgumentCaptor.capture())).thenReturn(bufferedImage);

        videoConsumer.accept(event);

        assertEquals("no-video.png", Path.of(urlArgumentCaptor.getValue().toString()).getFileName().toString());
        verify(encoder).encodeImage(bufferedImage);
        verify(encoder).finish();
    }

    @Test
    @DisplayName("accept shouldn't do nothing when video recording is disabled")
    public void acceptDisabled() throws IOException {
        when(event.getContext()).thenReturn(extensionContext);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(true);

        videoConsumer.accept(event);

        verify(encoder, never()).encodeImage(any());
    }

    @Test
    @DisplayName("chooseDimensionFor should return a new Dimension based on the provided video dimension")
    public void chooseDimensionFor() {
        when(video.getWidth()).thenReturn(1);
        when(video.getHeight()).thenReturn(3);

        final Dimension actual = videoConsumer.chooseDimensionFor(webDriver, video);
        assertEquals(2, actual.getWidth());
        assertEquals(4, actual.getHeight());
    }

    @DisplayName("chooseDimensionFor should return a new Dimension based on webDriver's dimension, if at least one is lte 0")
    @ParameterizedTest(name = "with width {0} and height {1}")
    @MethodSource("dimensionProvider")
    public void chooseDimensionForProvided(final int width, final int height) {
        final int menuBarsHeight = 123;

        reset(video);
        when(video.getWidth()).thenReturn(width);

        if (width >= 1) {   // short-circuit
            when(video.getHeight()).thenReturn(height);
        }
        when(webDriver.manage()).thenReturn(options);
        when(options.window()).thenReturn(window);
        when(window.getSize()).thenReturn(dimension);
        when(dimension.getWidth()).thenReturn(WIDTH);
        when(dimension.getHeight()).thenReturn(HEIGHT);
        when(video.getMenuBarsHeight()).thenReturn(menuBarsHeight);

        final Dimension actual = videoConsumer.chooseDimensionFor(webDriver, video);

        assertEquals(EVEN_WIDTH, actual.getWidth());
        assertEquals(EVEN_HEIGHT - menuBarsHeight - 1, actual.getHeight());
    }

    public static Stream<Arguments> dimensionProvider() {
        return Stream.of(
                arguments(0, 0),
                arguments(1, 0),
                arguments(0, 1)
        );
    }

    @DisplayName("makeItEven should increment the provided int if it's odd")
    @ParameterizedTest(name = "with i {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void makeItEven(final int i, final int expected) {
        assertEquals(expected, videoConsumer.makeItEven(i));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(0, 0),
                arguments(1, 2)
        );
    }

    @Test
    @DisplayName("isNewFrame should return true if the provided screenshot is new")
    public void isNewFrame() throws IOException {
        when(screenshot1.toPath()).thenReturn(screenshotPath1);
        when(Files.readAllBytes(screenshotPath1)).thenReturn(new byte[]{1, 2, 3});
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(new byte[]{4, 5, 6});
        when(frameDigests.stream()).thenReturn(frameDigestsStream);
        when(frameDigestsStream.noneMatch(any())).thenReturn(true);

        assertTrue(videoConsumer.isNewFrame(screenshot1));

        verify(frameDigests).add(byteArrayArgumentCaptor.capture());
        assertArrayEquals(new byte[]{1, 2, 3}, byteArrayArgumentCaptor.getAllValues().getFirst());
        assertArrayEquals(new byte[]{4, 5, 6}, byteArrayArgumentCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("isNewFrame should return false if the provided screenshot is not new")
    public void isNewFrameFalse() throws IOException {
        when(screenshot1.toPath()).thenReturn(screenshotPath1);
        when(Files.readAllBytes(screenshotPath1)).thenReturn(new byte[]{1, 2, 3});
        when(messageDigest.digest(byteArrayArgumentCaptor.capture())).thenReturn(new byte[]{4, 5, 6});
        when(frameDigests.stream()).thenReturn(frameDigestsStream);
        when(frameDigestsStream.noneMatch(any())).thenReturn(false);

        assertFalse(videoConsumer.isNewFrame(screenshot1));

        verify(frameDigests, never()).add(any());
        assertArrayEquals(new byte[]{1, 2, 3}, byteArrayArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("resize should resize the image and return it")
    public void resize() {
        final int width = 6;
        final int height = 100;

        MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, context) -> {
            assertEquals(EVEN_WIDTH, context.arguments().getFirst());
            assertEquals(EVEN_HEIGHT, context.arguments().get(1));
            assertEquals(TYPE_INT_RGB, context.arguments().get(2));

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
}
