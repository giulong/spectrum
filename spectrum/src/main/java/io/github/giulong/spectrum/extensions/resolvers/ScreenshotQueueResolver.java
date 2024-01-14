package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ScreenshotQueueResolver extends TypeBasedParameterResolver<BlockingQueue<File>> {

    public static final String SCREENSHOT_QUEUE = "screenshotQueue";

    @Override
    public BlockingQueue<File> resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", SCREENSHOT_QUEUE);
        final Video video = context.getRoot().getStore(GLOBAL).get(CONFIGURATION, Configuration.class).getVideo();
        if (video.isDisabled()) {
            log.debug("Video is disabled. Skipping resolution");
            return null;
        }

        final BlockingQueue<File> screenshotsQueue = new LinkedBlockingQueue<>();

        context.getStore(GLOBAL).put(SCREENSHOT_QUEUE, screenshotsQueue);
        return screenshotsQueue;
    }
}
