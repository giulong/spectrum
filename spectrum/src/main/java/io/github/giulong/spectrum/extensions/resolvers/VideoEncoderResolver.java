package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import io.github.giulong.spectrum.utils.video.VideoEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.ScreenshotQueueResolver.SCREENSHOT_QUEUE;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static io.github.giulong.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class VideoEncoderResolver extends TypeBasedParameterResolver<VideoEncoder> {

    public static final String VIDEO_ENCODER = "videoEncoder";

    @Override
    public VideoEncoder resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", VIDEO_ENCODER);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Configuration configuration = store.get(CONFIGURATION, Configuration.class);
        final Video video = configuration.getVideo();
        if (video.isDisabled()) {
            log.debug("Video is disabled. Skipping resolution");
            return null;
        }

        final TestData testData = store.get(TEST_DATA, TestData.class);
        final String className = testData.getClassName();
        final String methodName = testData.getMethodName();
        final Path videoPath = getVideoPathForCurrentTest(configuration.getExtent().getReportFolder(), className, methodName);
        final WebDriver webDriver = store.get(WEB_DRIVER, WebDriver.class);
        @SuppressWarnings("unchecked")
        final BlockingQueue<File> screenshotsQueue = (BlockingQueue<File>) store.get(SCREENSHOT_QUEUE, BlockingQueue.class);
        final VideoEncoder videoEncoder = new VideoEncoder(screenshotsQueue, className, methodName, videoPath.toFile(), video, webDriver);

        videoEncoder.start();
        store.put(VIDEO_ENCODER, videoEncoder);

        return videoEncoder;
    }

    @SneakyThrows
    public Path getVideoPathForCurrentTest(final String reportsFolder, final String className, final String methodName) {
        final Path path = Path.of(reportsFolder, "videos", className, methodName).toAbsolutePath();
        Files.createDirectories(path);
        return path.resolve(String.format("%s.mp4", randomUUID()));
    }
}
