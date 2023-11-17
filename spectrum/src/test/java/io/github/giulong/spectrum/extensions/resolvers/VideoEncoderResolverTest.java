package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.video.VideoEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotQueueResolver.SCREENSHOT_QUEUE;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.extensions.resolvers.VideoEncoderResolver.VIDEO_ENCODER;
import static io.github.giulong.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoEncoderResolver")
class VideoEncoderResolverTest {

    private static final String UUID_REGEX = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.mp4";
    private static final String REPORTS_FOLDER = "reportsFolder";
    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private WebDriver webDriver;

    @Mock
    private LinkedBlockingQueue<File> blockingQueue;

    @InjectMocks
    private VideoEncoderResolver videoEncoderResolver;

    @BeforeEach
    public void beforeEach() throws IOException {
        final Path path = Files.createTempDirectory(REPORTS_FOLDER);
        path.toFile().deleteOnExit();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of VideoEncoder and start it")
    public void resolveParameter() {
        final Path path = Path.of(REPORTS_FOLDER, "videos", CLASS_NAME, METHOD_NAME).toAbsolutePath();

        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);
        when(extent.getReportFolder()).thenReturn(REPORTS_FOLDER);

        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getClassName()).thenReturn(CLASS_NAME);
        when(testData.getMethodName()).thenReturn(METHOD_NAME);
        when(store.get(SCREENSHOT_QUEUE, BlockingQueue.class)).thenReturn(blockingQueue);
        when(store.get(WEB_DRIVER, WebDriver.class)).thenReturn(webDriver);

        MockedConstruction<VideoEncoder> videoEncoderMockedConstruction = mockConstruction(VideoEncoder.class, (mock, context) -> {
            assertEquals(blockingQueue, context.arguments().get(0));
            assertEquals(CLASS_NAME, context.arguments().get(1));
            assertEquals(METHOD_NAME, context.arguments().get(2));
            assertEquals(path.toString(), ((File) context.arguments().get(3)).getParent());
            assertEquals(video, context.arguments().get(4));
            assertEquals(webDriver, context.arguments().get(5));
        });

        final VideoEncoder actual = videoEncoderResolver.resolveParameter(parameterContext, extensionContext);
        final VideoEncoder videoEncoder = videoEncoderMockedConstruction.constructed().get(0);

        verify(videoEncoder).start();
        verify(store).put(VIDEO_ENCODER, videoEncoder);
        assertEquals(videoEncoder, actual);
        assertTrue(Files.exists(path));

        videoEncoderMockedConstruction.close();
    }

    @Test
    @DisplayName("resolveParameter should return null when video recording is disabled")
    public void resolveParameterDisabled() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(true);

        assertNull(videoEncoderResolver.resolveParameter(parameterContext, extensionContext));
        verify(store, never()).put(eq(VIDEO_ENCODER), any(VideoEncoder.class));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return the path for the current test and create the directories")
    public void getVideoPathForCurrentTest() {
        final Path path = Path.of(REPORTS_FOLDER, "videos", CLASS_NAME, METHOD_NAME).toAbsolutePath();
        final Path actual = videoEncoderResolver.getVideoPathForCurrentTest(REPORTS_FOLDER, CLASS_NAME, METHOD_NAME);

        assertEquals(path, actual.getParent());
        assertThat(actual.getFileName().toString(), matchesPattern(UUID_REGEX));
        assertTrue(Files.exists(path));
    }
}