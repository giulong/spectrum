package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.ScreenshotWatcher;
import io.github.giulong.spectrum.utils.video.Video;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotQueueResolver.SCREENSHOT_QUEUE;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotWatcherResolver.SCREENSHOT_WATCHER;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScreenshotWatcherResolver")
class ScreenshotWatcherResolverTest {

    private static final String SCREENSHOT_FOLDER = "reportsFolder";

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @Mock
    private TestData testData;

    @Mock
    private LinkedBlockingQueue<File> blockingQueue;

    @InjectMocks
    private ScreenshotWatcherResolver screenshotWatcherResolver;

    @BeforeEach
    public void beforeEach() throws IOException {
        final Path path = Files.createTempDirectory(SCREENSHOT_FOLDER);
        path.toFile().deleteOnExit();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of ScreenshotWatcher")
    public void resolveParameter() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(store.get(TEST_DATA, TestData.class)).thenReturn(testData);
        when(testData.getScreenshotFolderPath()).thenReturn(Path.of(SCREENSHOT_FOLDER));
        when(store.get(SCREENSHOT_QUEUE, BlockingQueue.class)).thenReturn(blockingQueue);

        MockedConstruction<ScreenshotWatcher> screenshotWatcherMockedConstruction = mockConstruction(ScreenshotWatcher.class, (mock, context) -> {
            assertEquals(blockingQueue, context.arguments().get(0));
            assertEquals(Path.of(SCREENSHOT_FOLDER), context.arguments().get(1));
            assertInstanceOf(WatchService.class, context.arguments().get(2));
        });

        final ScreenshotWatcher actual = screenshotWatcherResolver.resolveParameter(parameterContext, extensionContext);
        final ScreenshotWatcher screenshotWatcher = screenshotWatcherMockedConstruction.constructed().get(0);

        verify(screenshotWatcher).start();
        verify(store).put(SCREENSHOT_WATCHER, screenshotWatcher);
        assertEquals(screenshotWatcher, actual);

        screenshotWatcherMockedConstruction.close();
    }

    @Test
    @DisplayName("resolveParameter should return null if video recording is disabled")
    public void resolveParameterDisabled() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(true);

        assertNull(screenshotWatcherResolver.resolveParameter(parameterContext, extensionContext));
        verify(store, never()).put(eq(SCREENSHOT_WATCHER), any(ScreenshotWatcher.class));
    }
}
