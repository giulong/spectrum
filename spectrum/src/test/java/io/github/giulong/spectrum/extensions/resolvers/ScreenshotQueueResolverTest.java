package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.video.Video;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotQueueResolver.SCREENSHOT_QUEUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScreenshotQueueResolver")
class ScreenshotQueueResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Video video;

    @InjectMocks
    private ScreenshotQueueResolver screenshotQueueResolver;

    @Test
    @DisplayName("resolveParameter should return an instance of BlockingQueue<File>")
    public void resolveParameter() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        //noinspection rawtypes
        MockedConstruction<LinkedBlockingQueue> blockingQueueMockedConstruction = mockConstruction(LinkedBlockingQueue.class);

        final BlockingQueue<File> actual = screenshotQueueResolver.resolveParameter(parameterContext, extensionContext);

        //noinspection unchecked
        final BlockingQueue<File> blockingQueue = (BlockingQueue<File>) blockingQueueMockedConstruction.constructed().getFirst();
        assertEquals(blockingQueue, actual);
        verify(store).put(SCREENSHOT_QUEUE, actual);

        blockingQueueMockedConstruction.close();
    }

    @Test
    @DisplayName("resolveParameter should return null if video recording is disabled")
    public void resolveParameterDisable() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(true);

        assertNull(screenshotQueueResolver.resolveParameter(parameterContext, extensionContext));

        verify(store, never()).put(eq(SCREENSHOT_QUEUE), any(BlockingQueue.class));
    }
}
