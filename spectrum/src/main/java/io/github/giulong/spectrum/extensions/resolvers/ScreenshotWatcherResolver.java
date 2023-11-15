package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Recording;
import io.github.giulong.spectrum.utils.video.ScreenshotWatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotQueueResolver.SCREENSHOT_QUEUE;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ScreenshotWatcherResolver extends TypeBasedParameterResolver<ScreenshotWatcher> {

    public static final String SCREENSHOT_WATCHER = "screenshotWatcher";

    @SneakyThrows
    @Override
    public ScreenshotWatcher resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", SCREENSHOT_WATCHER);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Configuration.Extent extent = store.get(CONFIGURATION, Configuration.class).getExtent();
        final Recording recording = extent.getVideo().getRecording();
        if (recording.isDisabled()) {
            log.debug("Video is disabled. Skipping resolution");
            return null;
        }

        final Path screenshotFolderPath = store.get(TEST_DATA, TestData.class).getScreenshotFolderPath();
        @SuppressWarnings("unchecked")
        final BlockingQueue<File> screenshotsQueue = (BlockingQueue<File>) store.get(SCREENSHOT_QUEUE, BlockingQueue.class);
        final ScreenshotWatcher screenshotWatcher = new ScreenshotWatcher(screenshotsQueue, screenshotFolderPath, FileSystems.getDefault().newWatchService(), recording);

        screenshotWatcher.start();
        store.put(SCREENSHOT_WATCHER, screenshotWatcher);

        return screenshotWatcher;
    }
}
