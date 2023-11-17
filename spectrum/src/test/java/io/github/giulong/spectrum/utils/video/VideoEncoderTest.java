package io.github.giulong.spectrum.utils.video;

import io.github.giulong.spectrum.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoEncoder")
class VideoEncoderTest {

    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";
    private static final int WIDTH = 11;
    private static final int HEIGHT = 11;
    private static final int EVEN_WIDTH = WIDTH + 1;
    private static final int EVEN_HEIGHT = HEIGHT + 1;

    private static MockedStatic<AWTSequenceEncoder> awtSequenceEncoderMockedStatic;
    private static MockedStatic<ImageIO> imageIOMockedStatic;

    @Mock
    private AWTSequenceEncoder encoder;

    @Mock
    private LinkedBlockingQueue<File> blockingQueue;

    @Mock
    private File videoFile;

    @Mock
    private File screenshot;

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
    private Dimension size;

    @Mock
    private Video video;

    private VideoEncoder videoEncoder;

    @BeforeEach
    public void beforeEach() throws IOException {
        awtSequenceEncoderMockedStatic = mockStatic(AWTSequenceEncoder.class);
        imageIOMockedStatic = mockStatic(ImageIO.class);
        when(video.getWidth()).thenReturn(WIDTH);
        when(video.getHeight()).thenReturn(HEIGHT);
        awtSequenceEncoderMockedStatic.when(() -> AWTSequenceEncoder.createSequenceEncoder(videoFile, 1)).thenReturn(encoder);

        videoEncoder = new VideoEncoder(blockingQueue, CLASS_NAME, METHOD_NAME, videoFile, video, webDriver);
    }

    @AfterEach
    public void afterEach() {
        awtSequenceEncoderMockedStatic.close();
        imageIOMockedStatic.close();
    }

    @Test
    @DisplayName("chooseDimensionFor should return a new Dimension based on webdriver dimension")
    public void chooseDimensionFor() throws IllegalAccessException {
        final Field dimension = ReflectionUtils.getField("dimension", videoEncoder);

        videoEncoder.chooseDimensionFor(webDriver);
        assertEquals(EVEN_WIDTH, ((Dimension) dimension.get(videoEncoder)).getWidth());
        assertEquals(EVEN_HEIGHT, ((Dimension) dimension.get(videoEncoder)).getHeight());
    }

    @DisplayName("chooseDimensionFor should return a new Dimension based on provided parameters, if at least one is lte 0")
    @ParameterizedTest(name = "with width {0} and height {1}")
    @MethodSource("dimensionProvider")
    public void chooseDimensionForProvided(final int width, final int height) throws NoSuchFieldException, IllegalAccessException {
        reset(video);
        when(video.getWidth()).thenReturn(width);

        if (width >= 1) {
            when(video.getHeight()).thenReturn(height);
        }
        when(webDriver.manage()).thenReturn(options);
        when(options.window()).thenReturn(window);
        when(window.getSize()).thenReturn(size);
        when(size.getWidth()).thenReturn(WIDTH);
        when(size.getHeight()).thenReturn(HEIGHT);

        final Field dimension = ReflectionUtils.getField("dimension", videoEncoder);

        videoEncoder.chooseDimensionFor(webDriver);
        assertEquals(EVEN_WIDTH, ((Dimension) dimension.get(videoEncoder)).getWidth());
        assertEquals(EVEN_HEIGHT, ((Dimension) dimension.get(videoEncoder)).getHeight());
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
        assertEquals(expected, videoEncoder.makeItEven(i));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(0, 0),
                arguments(1, 2)
        );
    }

    @Test
    @DisplayName("run should process while receiving the stop signal, then process all the remaining screenshots")
    public void run() throws InterruptedException, IOException {
        // processNext
        when(blockingQueue.poll(1, SECONDS)).thenReturn(screenshot);
        imageIOMockedStatic.when(() -> ImageIO.read(screenshot)).thenReturn(bufferedImage);

        // resize
        MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, context) -> {
            assertEquals(EVEN_WIDTH, context.arguments().get(0));
            assertEquals(EVEN_HEIGHT, context.arguments().get(1));
            assertEquals(TYPE_INT_RGB, context.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        when(blockingQueue.isEmpty()).thenReturn(false).thenReturn(true);   // we simulate there's still 1 screenshot to process

        StopSignaler stopSignaler = new StopSignaler(videoEncoder);
        stopSignaler.start();

        //noinspection CallToThreadRun
        videoEncoder.run();

        final BufferedImage resizedImage1 = bufferedImageMockedConstruction.constructed().get(0);
        final BufferedImage resizedImage2 = bufferedImageMockedConstruction.constructed().get(1);
        verify(encoder).encodeImage(resizedImage1);
        verify(encoder).encodeImage(resizedImage2);
        verify(encoder).finish();

        bufferedImageMockedConstruction.close();
    }

    @Test
    @DisplayName("processNext should poll the blocking queue, resize the screenshot and encode it in the video")
    public void processNext() throws InterruptedException, IOException {
        when(blockingQueue.poll(1, SECONDS)).thenReturn(screenshot);
        imageIOMockedStatic.when(() -> ImageIO.read(screenshot)).thenReturn(bufferedImage);

        // resize
        MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, context) -> {
            assertEquals(EVEN_WIDTH, context.arguments().get(0));
            assertEquals(EVEN_HEIGHT, context.arguments().get(1));
            assertEquals(TYPE_INT_RGB, context.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        videoEncoder.processNext();

        final BufferedImage resizedImage = bufferedImageMockedConstruction.constructed().get(0);
        verify(encoder).encodeImage(resizedImage);
        bufferedImageMockedConstruction.close();
    }

    @Test
    @DisplayName("processNext should do nothing if polling returns nothing")
    public void processNextNull() throws InterruptedException, IOException {
        when(blockingQueue.poll(1, SECONDS)).thenReturn(null);

        videoEncoder.processNext();

        verify(encoder, never()).encodeImage(any());
    }

    @Test
    @DisplayName("resize should resize the image and return it")
    public void resize() {
        MockedConstruction<BufferedImage> bufferedImageMockedConstruction = mockConstruction(BufferedImage.class, (mock, context) -> {
            assertEquals(EVEN_WIDTH, context.arguments().get(0));
            assertEquals(EVEN_HEIGHT, context.arguments().get(1));
            assertEquals(TYPE_INT_RGB, context.arguments().get(2));

            when(mock.createGraphics()).thenReturn(graphics2D);
        });

        final BufferedImage actual = videoEncoder.resize(bufferedImage);
        assertEquals(bufferedImageMockedConstruction.constructed().get(0), actual);

        verify(graphics2D).drawImage(bufferedImage, 0, 0, null);
        verify(graphics2D).dispose();

        bufferedImageMockedConstruction.close();
    }

    @Test
    @DisplayName("done should set the stop signal to true and join the thread")
    public void done() throws IllegalAccessException {
        final Field stopSignal = ReflectionUtils.getField("stopSignal", videoEncoder);

        videoEncoder.done();

        assertTrue(stopSignal.getBoolean(videoEncoder));
    }

    @AllArgsConstructor
    private static class StopSignaler extends Thread {

        private VideoEncoder videoEncoder;

        @SneakyThrows
        @Override
        public void run() {
            Thread.sleep(2000);
            videoEncoder.done();
        }
    }
}