package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.SpectrumEntity.HASH_ALGORITHM;
import static io.github.giulong.spectrum.enums.Result.DISABLED;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.ORIGINAL_DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.Comparator.comparingLong;

@Slf4j
@JsonView(Internal.class)
public class VideoConsumer extends EventsConsumer {

    private final ClassLoader classLoader = VideoConsumer.class.getClassLoader();
    private final Configuration configuration = Configuration.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();

    private byte[] lastFrameDigest;
    private MessageDigest messageDigest;

    @Override
    protected boolean shouldAccept(final Event event) {
        return !DISABLED.equals(event.getResult()) && !configuration.getVideo().isDisabled();
    }

    @SneakyThrows
    @Override
    public void accept(final Event event) {
        final Video video = configuration.getVideo();
        final ExtensionContext context = event.getContext();
        final TestData testData = contextManager.get(context, TEST_DATA, TestData.class);

        init();

        log.info("Generating video for test {}.{}", testData.getClassName(), testData.getMethodName());

        try (Stream<Path> screenshots = Files.walk(testData.getScreenshotFolderPath())) {
            final AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(getVideoPathFrom(testData).toFile(), 1);
            final List<File> frames = screenshots
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> filter(file, testData))
                    .filter(file -> !video.isSkipDuplicateFrames() || isNewFrame(file, testData))
                    .sorted(comparingLong(File::lastModified))
                    .toList();

            if (frames.isEmpty()) {
                log.debug("No frames were added to the video. Adding 'no-video.png'");
                final URL noVideoPng = Objects.requireNonNull(classLoader.getResource("no-video.png"));
                encoder.encodeImage(ImageIO.read(noVideoPng));
            } else {
                final Dimension dimension = chooseDimensionFor(contextManager.get(context, ORIGINAL_DRIVER, WebDriver.class), video);

                for (File frame : frames) {
                    encoder.encodeImage(resize(ImageIO.read(frame), dimension));
                }
            }

            encoder.finish();
        }
    }

    @SneakyThrows
    protected void init() {
        this.lastFrameDigest = null;
        this.messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
    }

    protected Path getVideoPathFrom(final TestData testData) {
        return testData.getVideoPath();
    }

    protected boolean filter(final File file, final TestData testData) {
        return true;
    }

    @SneakyThrows
    protected boolean isNewFrame(final File screenshot, final TestData testData) {
        final byte[] digest = messageDigest.digest(Files.readAllBytes(screenshot.toPath()));

        if (!Arrays.equals(lastFrameDigest, digest)) {
            lastFrameDigest = digest;
            return true;
        }

        log.trace("Discarding duplicate frame {}", screenshot.getName());
        return false;
    }

    Dimension chooseDimensionFor(final WebDriver driver, final Video video) {
        int width = video.getWidth();
        int height = video.getHeight();

        if (video.getWidth() < 1 || video.getHeight() < 1) {
            final Dimension size = driver.manage().window().getSize();
            width = size.getWidth();
            height = size.getHeight() - video.getMenuBarsHeight();
        }

        final int evenWidth = makeItEven(width);
        final int evenHeight = makeItEven(height);

        log.debug("Video dimensions: {}x{}", evenWidth, evenHeight);
        return new Dimension(evenWidth, evenHeight);
    }

    int makeItEven(final int i) {
        return i % 2 == 0 ? i : i + 1;
    }

    BufferedImage resize(final BufferedImage bufferedImage, final Dimension dimension) {
        final int width = dimension.getWidth();
        final int height = dimension.getHeight();
        final int minWidth = Math.min(width, bufferedImage.getWidth());
        final int minHeight = Math.min(height, bufferedImage.getHeight());
        final BufferedImage resizedImage = new BufferedImage(width, height, TYPE_INT_RGB);
        final Graphics2D graphics2D = resizedImage.createGraphics();

        log.trace("Resizing screenshot to {}x{}", minWidth, minHeight);
        graphics2D.drawImage(bufferedImage, 0, 0, minWidth, minHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}
